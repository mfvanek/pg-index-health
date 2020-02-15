/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithBloat;
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

/**
 * Implementation of {@code IndexMaintenance} which collects information from current host in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see io.github.mfvanek.pg.connection.HostAware
 * @see PgHost
 */
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

    private static final String INDEXES_WITH_BLOAT =
            "with indexes_data as (\n" +
                    "    select\n" +
                    "        pc.relname as inner_index_name,\n" +
                    "        pc.reltuples,\n" +
                    "        pc.relpages,\n" +
                    "        pi.indrelid as table_oid,\n" +
                    "        pi.indexrelid as index_oid,\n" +
                    "        coalesce(substring(array_to_string(pc.reloptions, ' ') from 'fillfactor=([0-9]+)')::smallint, 90) as fill_factor,\n" +
                    "        pi.indnatts,\n" +
                    "        string_to_array(textin(int2vectorout(pi.indkey)), ' ')::int[] as indkey,\n" +
                    "        pn.nspname\n" +
                    "    from\n" +
                    "        pg_catalog.pg_index pi\n" +
                    "        join pg_catalog.pg_class pc on pc.oid = pi.indexrelid\n" +
                    "        join pg_catalog.pg_namespace pn on pn.oid = pc.relnamespace\n" +
                    "    where\n" +
                    "        pc.relam = (select oid from pg_catalog.pg_am where amname = 'btree') and\n" +
                    "        pc.relpages > 0 and\n" +
                    "        pn.nspname = ?::text\n" +
                    "),\n" +
                    "nested_indexes_attributes as (\n" +
                    "    select\n" +
                    "        inner_index_name,\n" +
                    "        reltuples,\n" +
                    "        relpages,\n" +
                    "        table_oid,\n" +
                    "        index_oid,\n" +
                    "        fill_factor,\n" +
                    "        indkey,\n" +
                    "        nspname,\n" +
                    "        pg_catalog.generate_series(1, indnatts) as attpos\n" +
                    "    from indexes_data\n" +
                    "),\n" +
                    "named_indexes_attributes as (\n" +
                    "    select\n" +
                    "        ic.table_oid,\n" +
                    "        ic.inner_index_name,\n" +
                    "        ic.attpos,\n" +
                    "        ic.indkey,\n" +
                    "        ic.indkey[ic.attpos],\n" +
                    "        ic.reltuples,\n" +
                    "        ic.relpages,\n" +
                    "        ic.index_oid,\n" +
                    "        ic.fill_factor,\n" +
                    "        coalesce(a1.attnum, a2.attnum) as attnum,\n" +
                    "        coalesce(a1.attname, a2.attname) as attname,\n" +
                    "        coalesce(a1.atttypid, a2.atttypid) as atttypid,\n" +
                    "        ic.nspname,\n" +
                    "        case when a1.attnum is null then ic.inner_index_name else ct.relname end as attrelname\n" +
                    "    from\n" +
                    "        nested_indexes_attributes ic\n" +
                    "        join pg_catalog.pg_class ct on ct.oid = ic.table_oid\n" +
                    "        left join pg_catalog.pg_attribute a1 on ic.indkey[ic.attpos] <> 0 and a1.attrelid = ic.table_oid and a1.attnum = ic.indkey[ic.attpos]\n" +
                    "        left join pg_catalog.pg_attribute a2 on ic.indkey[ic.attpos] = 0 and a2.attrelid = ic.index_oid and a2.attnum = ic.attpos\n" +
                    "),\n" +
                    "rows_data_stats as (\n" +
                    "    select\n" +
                    "        i.table_oid,\n" +
                    "        i.reltuples,\n" +
                    "        i.relpages,\n" +
                    "        i.index_oid,\n" +
                    "        i.fill_factor,\n" +
                    "        current_setting('block_size')::bigint as block_size,\n" +
                    "        /* max_align: 4 on 32bits, 8 on 64bits (and mingw32 ?) */\n" +
                    "        case when version() ~ 'mingw32' or version() ~ '64-bit|x86_64|ppc64|ia64|amd64' then 8 else 4 end as max_align,\n" +
                    "        /* per page header, fixed size: 20 for 7.x, 24 for others */\n" +
                    "        24 as page_header_size,\n" +
                    "        /* per page btree opaque data */\n" +
                    "        16 as page_opaque_data_size,\n" +
                    "        /* per tuple header: add indexattributebitmapdata if some cols are null-able */\n" +
                    "        case when max(coalesce(s.null_frac, 0)) = 0 then 2 /* indextupledata size */\n" +
                    "            else 2 + ((32 + 8 - 1) / 8) /* indextupledata size + indexattributebitmapdata size (max num filed per index + 8 - 1 /8) */\n" +
                    "            end as index_tuple_header_size,\n" +
                    "        /* remove null values and save space using it fractional part from stats */\n" +
                    "        sum((1 - coalesce(s.null_frac, 0)) * coalesce(s.avg_width, 0)) as null_data_width\n" +
                    "    from\n" +
                    "        named_indexes_attributes i\n" +
                    "        join pg_catalog.pg_stats s on s.schemaname = i.nspname and s.tablename = i.attrelname and s.attname = i.attname\n" +
                    "    group by 1, 2, 3, 4, 5, 6, 7, 8, 9\n" +
                    "),\n" +
                    "rows_header_stats as (\n" +
                    "    select\n" +
                    "        max_align,\n" +
                    "        block_size,\n" +
                    "        reltuples,\n" +
                    "        relpages,\n" +
                    "        index_oid,\n" +
                    "        fill_factor,\n" +
                    "        table_oid,\n" +
                    "        (index_tuple_header_size + max_align\n" +
                    "             /* add padding to the index tuple header to align on max_align */\n" +
                    "             -\n" +
                    "         case when index_tuple_header_size % max_align = 0 then max_align else index_tuple_header_size % max_align end\n" +
                    "             + null_data_width + max_align\n" +
                    "            /* add padding to the data to align on max_align */\n" +
                    "            - case\n" +
                    "                  when null_data_width = 0 then 0\n" +
                    "                  when null_data_width::integer % max_align = 0 then max_align\n" +
                    "                  else null_data_width::integer % max_align end\n" +
                    "            )::numeric as null_data_header_width,\n" +
                    "        page_header_size,\n" +
                    "        page_opaque_data_size\n" +
                    "    from rows_data_stats\n" +
                    "),\n" +
                    "relation_stats as (\n" +
                    "    select\n" +
                    "        /* itemiddata size + computed avg size of a tuple (nulldatahdrwidth) */\n" +
                    "        coalesce(1 +\n" +
                    "                 ceil(reltuples / floor((block_size - page_opaque_data_size - page_header_size) * fill_factor / (100 * (4 + null_data_header_width)::float))),\n" +
                    "            0)::bigint as estimated_pages_count,\n" +
                    "        block_size,\n" +
                    "        table_oid::regclass::text as table_name,\n" +
                    "        index_oid::regclass::text as index_name,\n" +
                    "        pg_relation_size(index_oid) as index_size,\n" +
                    "        relpages\n" +
                    "    from rows_header_stats\n" +
                    "),\n" +
                    "corrected_relation_stats as (\n" +
                    "    select\n" +
                    "        table_name,\n" +
                    "        index_name,\n" +
                    "        index_size,\n" +
                    "        block_size,\n" +
                    "        relpages,\n" +
                    "        case when relpages - estimated_pages_count > 0 then relpages - estimated_pages_count else 0 end as pages_ff_diff\n" +
                    "    from relation_stats\n" +
                    "),\n" +
                    " bloat_stats as (\n" +
                    "    select\n" +
                    "        table_name,\n" +
                    "        index_name,\n" +
                    "        index_size,\n" +
                    "        block_size * pages_ff_diff as bloat_size,\n" +
                    "        round(100 * block_size * pages_ff_diff / index_size::float)::integer as bloat_percentage\n" +
                    "     from\n" +
                    "        corrected_relation_stats\n" +
                    " )\n" +
                    "select *\n" +
                    "from bloat_stats\n" +
                    "where bloat_percentage >= ?::integer\n" +
                    "order by table_name, index_name;";

    private final PgConnection pgConnection;

    public IndexMaintenanceImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Index> getInvalidIndexes(@Nonnull final PgContext pgContext) {
        return executeQuery(INVALID_INDEXES_SQL, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final String indexName = rs.getString("index_name");
            return Index.of(tableName, indexName);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull final PgContext pgContext) {
        return getDuplicatedOrIntersectedIndexes(
                DUPLICATED_INDEXES_SQL, pgContext, "duplicated_indexes");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull final PgContext pgContext) {
        return getDuplicatedOrIntersectedIndexes(
                INTERSECTED_INDEXES_SQL, pgContext, "intersected_indexes");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Table> getTablesWithoutPrimaryKey(@Nonnull final PgContext pgContext) {
        return executeQuery(TABLES_WITHOUT_PRIMARY_KEY, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final long tableSize = rs.getLong("table_size");
            return Table.of(tableName, tableSize);
        });
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext) {
        return executeQueryWithBloatThreshold(INDEXES_WITH_BLOAT, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final String indexName = rs.getString("index_name");
            final long indexSize = rs.getLong("index_size");
            final long bloatSize = rs.getLong("bloat_size");
            final int bloatPercentage = rs.getInt("bloat_percentage");
            return IndexWithBloat.of(tableName, indexName, indexSize, bloatSize, bloatPercentage);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgHost getHost() {
        return pgConnection.getHost();
    }

    private <T> List<T> executeQuery(@Nonnull final String sqlQuery,
                                     @Nonnull final PgContext pgContext,
                                     @Nonnull final ResultSetExtractor<T> rse) {
        return QueryExecutor.executeQueryWithSchema(pgConnection, pgContext, sqlQuery, rse);
    }

    private <T> List<T> executeQueryWithBloatThreshold(@Nonnull final String sqlQuery,
                                                       @Nonnull final PgContext pgContext,
                                                       @Nonnull final ResultSetExtractor<T> rse) {
        return QueryExecutor.executeQueryWithBloatThreshold(pgConnection, pgContext, sqlQuery, rse);
    }
}
