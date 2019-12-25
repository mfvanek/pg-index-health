/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import io.github.mfvanek.pg.utils.QueryExecutor;
import io.github.mfvanek.pg.utils.ResultSetExtractor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IndexMaintenanceImpl implements IndexMaintenance {

    private static final String INVALID_INDEXES_SQL =
            "select x.indrelid::regclass as table_name,\n" +
                    "    x.indexrelid::regclass as index_name\n" +
                    "from pg_catalog.pg_index x\n" +
                    "    join pg_catalog.pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid\n" +
                    "where psai.schemaname = ?::text\n" +
                    "  and x.indisvalid = false;";

    private static final String DUPLICATED_INDEXES_SQL =
            "select table_name,\n" +
                    "    string_agg('idx=' || idx::text || ', size=' || pg_relation_size(idx), '; ') as duplicated_indexes\n" +
                    "from (\n" +
                    "    select x.indexrelid::regclass as idx,\n" +
                    "        x.indrelid::regclass as table_name,\n" +
                    "        (x.indrelid::text || ' ' || x.indclass::text || ' ' || x.indkey::text || ' ' ||\n" +
                    "         coalesce(pg_get_expr(x.indexprs, x.indrelid), '') || e' ' ||\n" +
                    "         coalesce(pg_get_expr(x.indpred, x.indrelid), '')) as key\n" +
                    "    from pg_catalog.pg_index x\n" +
                    "             join pg_catalog.pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid\n" +
                    "    where psai.schemaname = ?::text\n" +
                    ") sub\n" +
                    "group by table_name, key\n" +
                    "having count(*) > 1\n" +
                    "order by table_name, sum(pg_relation_size(idx)) desc;";

    private static final String INTERSECTED_INDEXES_SQL =
            "select a.indrelid::regclass as table_name,\n" +
                    "       'idx=' || a.indexrelid::regclass || ', size=' || pg_relation_size(a.indexrelid) || '; idx=' || b.indexrelid::regclass || ', size=' || pg_relation_size(b.indexrelid) as intersected_indexes\n" +
                    "from\n" +
                    "    (select *, array_to_string(indkey, ' ') as cols from pg_catalog.pg_index) as a\n" +
                    "        join (select *, array_to_string(indkey, ' ') as cols from pg_catalog.pg_index) as b\n" +
                    "             on (a.indrelid = b.indrelid and a.indexrelid > b.indexrelid and (\n" +
                    "                     (a.cols like b.cols || '%' and coalesce(substr(a.cols, length(b.cols) + 1, 1), ' ') = ' ') or\n" +
                    "                     (b.cols like a.cols || '%' and coalesce(substr(b.cols, length(a.cols) + 1, 1), ' ') = ' ')))\n" +
                    "        join pg_catalog.pg_stat_all_indexes psai on a.indexrelid = psai.indexrelid\n" +
                    "where psai.schemaname = ?::text\n" +
                    "order by a.indrelid::regclass::text;";

    private static final String UNUSED_INDEXES_SQL =
            "with foreign_key_indexes as (\n" +
                    "    select i.indexrelid\n" +
                    "    from pg_catalog.pg_constraint c\n" +
                    "             join lateral unnest(c.conkey) with ordinality as u(attnum, attposition) on true\n" +
                    "             join pg_catalog.pg_index i on i.indrelid = c.conrelid and (c.conkey::int[] <@ i.indkey::int[])\n" +
                    "    where c.contype = 'f'\n" +
                    ")\n" +
                    "select psui.relid::regclass::text as table_name,\n" +
                    "    psui.indexrelid::regclass::text as index_name,\n" +
                    "    pg_relation_size(i.indexrelid) as index_size,\n" +
                    "    psui.idx_scan as index_scans\n" +
                    "from pg_catalog.pg_stat_user_indexes psui\n" +
                    "         join pg_catalog.pg_index i on psui.indexrelid = i.indexrelid\n" +
                    "where psui.schemaname = ?::text\n" +
                    "  and not i.indisunique\n" +
                    "  and i.indexrelid not in (select * from foreign_key_indexes) /*retain indexes on foreign keys*/\n" +
                    "  and psui.idx_scan < 50::integer\n" +
                    "order by psui.relname, pg_relation_size(i.indexrelid) desc;";

    private static final String FOREIGN_KEYS_WITHOUT_INDEX =
            "select c.conrelid::regclass as table_name,\n" +
                    "    string_agg(col.attname, ', ' order by u.attposition) as columns,\n" +
                    "    c.conname as constraint_name\n" +
                    "from pg_catalog.pg_constraint c\n" +
                    "         join lateral unnest(c.conkey) with ordinality as u(attnum, attposition) on true\n" +
                    "         join pg_catalog.pg_class t on (c.conrelid = t.oid)\n" +
                    "         join pg_catalog.pg_namespace nsp on nsp.oid = t.relnamespace\n" +
                    "         join pg_catalog.pg_attribute col on (col.attrelid = t.oid and col.attnum = u.attnum)\n" +
                    "where c.contype = 'f'\n" +
                    "  and nsp.nspname = ?::text\n" +
                    "  and not exists(\n" +
                    "        select 1\n" +
                    "        from pg_catalog.pg_index pi\n" +
                    "        where pi.indrelid = c.conrelid\n" +
                    "          and (c.conkey::int[] <@ pi.indkey::int[]) /*all columns of foreign key have to present in index*/\n" +
                    "          and array_position(pi.indkey::int[], (c.conkey::int[])[1]) = 0 /*ordering of columns in foreign key and in index is the same*/\n" +
                    "    )\n" +
                    "group by c.conrelid, c.conname, c.oid\n" +
                    "order by (c.conrelid::regclass)::text, columns;";

    private static final String TABLES_WITH_MISSING_INDEXES =
            "with tables_without_indexes as (\n" +
                    "    select psat.relid::regclass::text as table_name,\n" +
                    "        pg_table_size(psat.relid) as table_size,\n" +
                    "        coalesce(psat.seq_scan, 0) - coalesce(psat.idx_scan, 0) as too_much_seq,\n" +
                    "        coalesce(psat.seq_scan, 0) as seq_scan,\n" +
                    "        coalesce(psat.idx_scan, 0) as idx_scan\n" +
                    "    from pg_catalog.pg_stat_all_tables psat\n" +
                    "    where psat.schemaname = ?::text\n" +
                    ")\n" +
                    "select table_name,\n" +
                    "    table_size,\n" +
                    "    seq_scan,\n" +
                    "    idx_scan\n" +
                    "from tables_without_indexes\n" +
                    "where (seq_scan + idx_scan) > 100::integer /*table in use*/\n" +
                    "  and too_much_seq > 0 /*too much sequential scans*/\n" +
                    "order by table_name, too_much_seq desc;";

    private static final String TABLES_WITHOUT_PRIMARY_KEY =
            "select psat.relid::regclass::text as table_name,\n" +
                    "    pg_table_size(psat.relid) as table_size\n" +
                    "from pg_catalog.pg_stat_all_tables psat\n" +
                    "where psat.schemaname = ?::text\n" +
                    "  and psat.relid::regclass not in (\n" +
                    "    select c.conrelid::regclass as table_name\n" +
                    "    from pg_catalog.pg_constraint c\n" +
                    "    where c.contype = 'p')\n" +
                    "order by psat.relname::text;";

    private static final String INDEXES_WITH_NULL_VALUES =
            "select x.indrelid::regclass as table_name,\n" +
                    "    x.indexrelid::regclass as index_name,\n" +
                    "    string_agg(a.attname, ', ') as nullable_fields,\n" +
                    "    pg_relation_size(x.indexrelid) as index_size\n" +
                    "from pg_catalog.pg_index x\n" +
                    "         join pg_catalog.pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid\n" +
                    "         join pg_catalog.pg_attribute a ON a.attrelid = x.indrelid AND a.attnum = any(x.indkey)\n" +
                    "where not x.indisunique\n" +
                    "  and not a.attnotnull\n" +
                    "  and psai.schemaname = ?::text\n" +
                    "  and array_position(x.indkey, a.attnum) = 0 /*only for first segment*/\n" +
                    "  and (x.indpred is null or (position(lower(a.attname) in lower(pg_get_expr(x.indpred, x.indrelid))) = 0))\n" +
                    "group by x.indrelid, x.indexrelid, x.indpred\n" +
                    "order by table_name, index_name;";

    private final PgConnection pgConnection;

    public IndexMaintenanceImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection);
    }

    @Nonnull
    @Override
    public List<Index> getInvalidIndexes(@Nonnull final PgContext pgContext) {
        return executeQuery(INVALID_INDEXES_SQL, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final String indexName = rs.getString("index_name");
            return Index.of(tableName, indexName);
        });
    }

    @Nonnull
    @Override
    public List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull final PgContext pgContext) {
        return getDuplicatedOrIntersectedIndexes(
                DUPLICATED_INDEXES_SQL, pgContext, "duplicated_indexes");
    }

    @Nonnull
    @Override
    public List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull final PgContext pgContext) {
        return getDuplicatedOrIntersectedIndexes(
                INTERSECTED_INDEXES_SQL, pgContext, "intersected_indexes");
    }

    @Nonnull
    @Override
    public List<UnusedIndex> getPotentiallyUnusedIndexes(@Nonnull final PgContext pgContext) {
        return executeQuery(UNUSED_INDEXES_SQL, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final String indexName = rs.getString("index_name");
            final long indexSize = rs.getLong("index_size");
            final long indexScans = rs.getLong("index_scans");
            return UnusedIndex.of(tableName, indexName, indexSize, indexScans);
        });
    }

    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull final PgContext pgContext) {
        return executeQuery(FOREIGN_KEYS_WITHOUT_INDEX, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final String constraintName = rs.getString("constraint_name");
            final String columnsAsString = rs.getString("columns");
            final String[] columns = columnsAsString.split(", ");
            return ForeignKey.of(tableName, constraintName, Arrays.asList(columns));
        });
    }

    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull final PgContext pgContext) {
        return executeQuery(TABLES_WITH_MISSING_INDEXES, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final long tableSize = rs.getLong("table_size");
            final long seqScans = rs.getLong("seq_scan");
            final long indexScans = rs.getLong("idx_scan");
            return TableWithMissingIndex.of(tableName, tableSize, seqScans, indexScans);
        });
    }

    @Nonnull
    @Override
    public List<Table> getTablesWithoutPrimaryKey(@Nonnull final PgContext pgContext) {
        return executeQuery(TABLES_WITHOUT_PRIMARY_KEY, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final long tableSize = rs.getLong("table_size");
            return Table.of(tableName, tableSize);
        });
    }

    @Nonnull
    @Override
    public List<IndexWithNulls> getIndexesWithNullValues(@Nonnull final PgContext pgContext) {
        return executeQuery(INDEXES_WITH_NULL_VALUES, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final String indexName = rs.getString("index_name");
            final long indexSize = rs.getLong("index_size");
            final String nullableField = rs.getString("nullable_fields");
            return IndexWithNulls.of(tableName, indexName, indexSize, nullableField);
        });
    }

    @Nonnull
    private List<DuplicatedIndexes> getDuplicatedOrIntersectedIndexes(@Nonnull final String sqlQuery,
                                                                      @Nonnull final PgContext pgContext,
                                                                      @Nonnull final String columnName) {
        return executeQuery(sqlQuery, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final String duplicatedAsString = rs.getString(columnName);
            return DuplicatedIndexes.of(tableName, duplicatedAsString);
        });
    }

    @Override
    @Nonnull
    public PgHost getHost() {
        return pgConnection.getHost();
    }

    private <T> List<T> executeQuery(@Nonnull final String sqlQuery,
                                     @Nonnull final PgContext pgContext,
                                     @Nonnull final ResultSetExtractor<T> rse) {
        return QueryExecutor.executeQuery(pgConnection, pgContext, sqlQuery, rse);
    }
}
