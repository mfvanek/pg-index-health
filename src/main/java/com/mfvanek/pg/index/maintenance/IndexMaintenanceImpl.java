/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.model.DuplicatedIndices;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.TableWithoutPrimaryKey;
import com.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IndexMaintenanceImpl implements IndexMaintenance {

    private static final String INVALID_INDICES_SQL =
            "select x.indrelid::regclass as table_name, x.indexrelid::regclass as index_name\n" +
                    "from pg_index x\n" +
                    "join pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid\n" +
                    "where x.indisvalid = false and psai.schemaname = 'public'::text;";

    private static final String DUPLICATED_INDICES_SQL =
            "select table_name,\n" +
                    "       string_agg('idx=' || idx::text || ', size=' || pg_relation_size(idx), '; ') as duplicated_indices\n" +
                    "from (\n" +
                    "       select x.indexrelid::regclass as idx, x.indrelid::regclass as table_name,\n" +
                    "              (x.indrelid::text ||' '|| x.indclass::text ||' '|| x.indkey::text ||' '||\n" +
                    "               coalesce(pg_get_expr(x.indexprs, x.indrelid),'')||e' ' ||\n" +
                    "               coalesce(pg_get_expr(x.indpred, x.indrelid),'')) as key\n" +
                    "       from pg_index x\n" +
                    "       join pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid\n" +
                    "       where psai.schemaname = 'public'::text\n" +
                    "     ) sub\n" +
                    "group by table_name, key having count(*) > 1\n" +
                    "order by table_name, sum(pg_relation_size(idx)) desc;";

    private static final String INTERSECTED_INDICES_SQL =
            "select a.indrelid::regclass as table_name,\n" +
                    "       'idx=' || a.indexrelid::regclass || ', size=' || pg_relation_size(a.indexrelid) || '; idx=' ||\n" +
                    "           b.indexrelid::regclass || ', size=' || pg_relation_size(b.indexrelid) as intersected_indices\n" +
                    "from (\n" +
                    "    select *, array_to_string(indkey, ' ') as cols from pg_index) as a\n" +
                    "    join (select *, array_to_string(indkey, ' ') as cols from pg_index) as b\n" +
                    "        on (a.indrelid = b.indrelid and a.indexrelid > b.indexrelid and (\n" +
                    "            (a.cols like b.cols||'%' and coalesce(substr(a.cols, length(b.cols)+1, 1), ' ') = ' ') or\n" +
                    "            (b.cols like a.cols||'%' and coalesce(substr(b.cols, length(a.cols)+1, 1), ' ') = ' ')))\n" +
                    "order by a.indrelid::regclass::text;";

    private static final String UNUSED_INDICES_SQL =
            "with foreign_key_indices as (\n" +
                    "    select i.indexrelid\n" +
                    "    from pg_constraint c\n" +
                    "        join lateral unnest(c.conkey) with ordinality as u(attnum, attposition) on true\n" +
                    "        join pg_index i on i.indrelid = c.conrelid and (c.conkey::int[] <@ indkey::int[])\n" +
                    "    where c.contype = 'f'\n" +
                    ")\n" +
                    "select psui.relname as table_name,\n" +
                    "       psui.indexrelname as index_name,\n" +
                    "       pg_relation_size(i.indexrelid) as index_size,\n" +
                    "       psui.idx_scan as index_scans\n" +
                    "from pg_stat_user_indexes psui\n" +
                    "    join pg_index i on psui.indexrelid = i.indexrelid\n" +
                    "where\n" +
                    "      psui.schemaname = 'public'::text and not i.indisunique and\n" +
                    "      i.indexrelid not in (select * from foreign_key_indices) and /*retain indices on foreign keys*/\n" +
                    "      psui.idx_scan < 50::integer and\n" +
                    "      pg_relation_size(psui.relid) >= 5::integer * 8192 and /*skip small tables*/\n" +
                    "      pg_relation_size(psui.indexrelid) >= 5::integer * 8192 /*skip small indices*/\n" +
                    "order by psui.relname, pg_relation_size(i.indexrelid) desc;";

    private static final String FOREIGN_KEYS_WITHOUT_INDEX =
            "select c.conrelid::regclass as table_name,\n" +
                    "       string_agg(col.attname, ', ' order by u.attposition) as columns,\n" +
                    "       c.conname as constraint_name\n" +
                    "from pg_constraint c\n" +
                    "    join lateral unnest(c.conkey) with ordinality as u(attnum, attposition) on true\n" +
                    "    join pg_class t on (c.conrelid = t.oid)\n" +
                    "    join pg_namespace nsp on nsp.oid = t.relnamespace\n" +
                    "    join pg_attribute col on (col.attrelid = t.oid and col.attnum = u.attnum)\n" +
                    "where contype = 'f' and\n" +
                    "      nsp.nspname = 'public'::text and\n" +
                    "      not exists (\n" +
                    "          select 1 from pg_index\n" +
                    "          where indrelid = c.conrelid and\n" +
                    "                (c.conkey::int[] <@ indkey::int[]) and /*все поля внешнего ключа должны быть в индексе*/\n" +
                    "                array_position(indkey::int[], (c.conkey::int[])[1]) = 0 /*порядок полей во внешнем ключе и в индексе совпадает*/\n" +
                    "      )\n" +
                    "group by c.conrelid, c.conname, c.oid\n" +
                    "order by (c.conrelid::regclass)::text, columns;";

    private static final String TABLES_WITH_MISSING_INDICES =
            "with tables_without_indices as (\n" +
                    "    select\n" +
                    "        relname::text as table_name,\n" +
                    "        coalesce(seq_scan, 0) - coalesce(idx_scan, 0) as too_much_seq,\n" +
                    "        pg_relation_size(relname::regclass) as table_size,\n" +
                    "        coalesce(seq_scan, 0) as seq_scan,\n" +
                    "        coalesce(idx_scan, 0) as idx_scan\n" +
                    "    from pg_stat_all_tables\n" +
                    "    where\n" +
                    "          schemaname = 'public'::text and\n" +
                    "          pg_relation_size(relname::regclass) > 5::integer * 8192 and /*skip small tables*/\n" +
                    "          relname not in ('databasechangelog')\n" +
                    ")\n" +
                    "select table_name,\n" +
                    "       seq_scan,\n" +
                    "       idx_scan\n" +
                    "from tables_without_indices\n" +
                    "where (seq_scan + idx_scan) > 100::integer and /*table in use*/\n" +
                    "      too_much_seq > 0 -- too much sequential scans\n" +
                    "order by table_name, too_much_seq desc;";

    private static final String TABLES_WITHOUT_PRIMARY_KEYS =
            "select tablename as table_name\n" +
                    "from pg_tables\n" +
                    "where\n" +
                    "    schemaname = 'public'::text and\n" +
                    "    tablename not in (\n" +
                    "    select c.conrelid::regclass::text as table_name\n" +
                    "    from pg_constraint c\n" +
                    "    where contype = 'p') and\n" +
                    "    tablename not in ('databasechangelog')\n" +
                    "order by tablename;";

    private static final String INDICES_WITH_NULL_VALUES =
            "select x.indrelid::regclass as table_name,\n" +
                    "       x.indexrelid::regclass as index_name,\n" +
                    "       string_agg(a.attname, ', ') as nullable_fields,\n" +
                    "       pg_relation_size(x.indexrelid) as index_size\n" +
                    "from pg_index x\n" +
                    "    join pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid\n" +
                    "    join pg_attribute a ON a.attrelid = x.indrelid AND a.attnum = any(x.indkey)\n" +
                    "where not x.indisunique and\n" +
                    "      not a.attnotnull and\n" +
                    "      psai.schemaname = 'public'::text and\n" +
                    "      array_position(x.indkey, a.attnum) = 0 and -- only for first segment\n" +
                    "      (x.indpred is null or (position(lower(a.attname) in lower(pg_get_expr(x.indpred, x.indrelid))) = 0))\n" +
                    "group by x.indrelid, x.indexrelid, x.indpred\n" +
                    "order by table_name, index_name;";

    private final DataSource dataSource;

    public IndexMaintenanceImpl(@Nonnull final DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Nonnull
    @Override
    public List<Index> getInvalidIndices() {
        final List<Index> invalidIndices = new ArrayList<>();
        executeQuery(INVALID_INDICES_SQL, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                final String indexName = rs.getString("index_name");
                invalidIndices.add(Index.of(tableName, indexName));
            }
        });
        return invalidIndices;
    }

    @Nonnull
    @Override
    public List<DuplicatedIndices> getDuplicatedIndices() {
        return getDuplicatedOrIntersectedIndices(DUPLICATED_INDICES_SQL, "duplicated_indices");
    }

    @Nonnull
    @Override
    public List<DuplicatedIndices> getIntersectedIndices() {
        return getDuplicatedOrIntersectedIndices(INTERSECTED_INDICES_SQL, "intersected_indices");
    }

    @Nonnull
    @Override
    public List<UnusedIndex> getPotentiallyUnusedIndices() {
        final List<UnusedIndex> unusedIndices = new ArrayList<>();
        executeQuery(UNUSED_INDICES_SQL, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                final String indexName = rs.getString("index_name");
                final long indexSize = rs.getLong("index_size");
                final long indexScans = rs.getLong("index_scans");
                unusedIndices.add(UnusedIndex.of(tableName, indexName, indexSize, indexScans));
            }
        });
        return unusedIndices;
    }

    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        final List<ForeignKey> foreignKeysWithoutIndex = new ArrayList<>();
        executeQuery(FOREIGN_KEYS_WITHOUT_INDEX, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                final String constraintName = rs.getString("constraint_name");
                final String columnsAsString = rs.getString("columns");
                final String[] columns = columnsAsString.split(", ");
                foreignKeysWithoutIndex.add(ForeignKey.of(tableName, constraintName, Arrays.asList(columns)));
            }
        });
        return foreignKeysWithoutIndex;
    }

    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndices() {
        final List<TableWithMissingIndex> tableWithMissingIndices = new ArrayList<>();
        executeQuery(TABLES_WITH_MISSING_INDICES, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                final long seqScans = rs.getLong("seq_scan");
                final long indexScans = rs.getLong("idx_scan");
                tableWithMissingIndices.add(TableWithMissingIndex.of(tableName, seqScans, indexScans));
            }
        });
        return tableWithMissingIndices;
    }

    @Nonnull
    @Override
    public List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey() {
        final List<TableWithoutPrimaryKey> tableWithoutPrimaryKeys = new ArrayList<>();
        executeQuery(TABLES_WITHOUT_PRIMARY_KEYS, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                tableWithoutPrimaryKeys.add(TableWithoutPrimaryKey.of(tableName));
            }
        });
        return tableWithoutPrimaryKeys;
    }

    @Nonnull
    @Override
    public List<IndexWithNulls> getIndicesWithNullValues() {
        final List<IndexWithNulls> indicesWithNulls = new ArrayList<>();
        executeQuery(INDICES_WITH_NULL_VALUES, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                final String indexName = rs.getString("index_name");
                final long indexSize = rs.getLong("index_size");
                final String nullableField = rs.getString("nullable_fields");
                indicesWithNulls.add(IndexWithNulls.of(tableName, indexName, indexSize, nullableField));
            }
        });
        return indicesWithNulls;
    }

    @Nonnull
    private List<DuplicatedIndices> getDuplicatedOrIntersectedIndices(@Nonnull final String sqlQuery,
                                                                      @Nonnull final String columnName) {
        final List<DuplicatedIndices> indices = new ArrayList<>();
        executeQuery(sqlQuery, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                final String duplicatedAsString = rs.getString(columnName);
                indices.add(DuplicatedIndices.of(tableName, duplicatedAsString));
            }
        });
        return indices;
    }

    private void executeQuery(@Nonnull final String sqlQuery, ResultSetExtractor rse) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(Objects.requireNonNull(sqlQuery))) {
                rse.extractData(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
