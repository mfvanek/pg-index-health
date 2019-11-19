/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.model.DuplicatedIndexes;
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
import java.util.List;
import java.util.Objects;

public class IndexMaintenanceImpl implements IndexMaintenance {

    private static final String INVALID_INDEXES_SQL =
            "select x.indrelid::regclass as table_name, x.indexrelid::regclass as index_name\n" +
                    "from pg_index x\n" +
                    "join pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid\n" +
                    "where x.indisvalid = false and psai.schemaname = 'public'::text;";

    private static final String DUPLICATED_INDEXES_SQL =
            "select table_name,\n" +
                    "       string_agg('idx=' || idx::text || ', size=' || pg_relation_size(idx), '; ') as duplicated_indexes\n" +
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

    private static final String INTERSECTED_INDEXES_SQL =
            "select a.indrelid::regclass as table_name, " +
                    "a.indexrelid::regclass as first_index, b.indexrelid::regclass as second_index, " +
                    "pg_relation_size(a.indexrelid) + pg_relation_size(b.indexrelid) as total_size " +
                    "from (select *, array_to_string(indkey, ' ') as cols from pg_index) as a " +
                    "join (select *, array_to_string(indkey, ' ') as cols from pg_index) as b on " +
                    "  (a.indrelid = b.indrelid and a.indexrelid > b.indexrelid and " +
                    "   (" +
                    "     (a.cols like b.cols||'%' and coalesce(substr(a.cols, length(b.cols)+1, 1), ' ') = ' ') or " +
                    "     (b.cols like a.cols||'%' and coalesce(substr(b.cols, length(a.cols)+1, 1), ' ') = ' ') " +
                    "     )" +
                    "  ) " +
                    "order by a.indrelid;";

    private static final String UNUSED_INDEXES_SQL =
            "with forein_key_indexes as ( " +
                    "  select i.indexrelid " +
                    "    from pg_constraint c " +
                    "    join lateral unnest(c.conkey) with ordinality as u(attnum, attposition) on true " +
                    "    join pg_index i on i.indrelid = c.conrelid and (c.conkey::int[] <@ indkey::int[]) " +
                    "    where c.contype = 'f' " +
                    ") " +
                    "select psui.relname as table_name, " +
                    "  psui.indexrelname as index_name, " +
                    "  pg_relation_size(i.indexrelid) as index_size, " +
                    "  psui.idx_scan as index_scans " +
                    "from pg_stat_user_indexes psui " +
                    "join pg_index i on psui.indexrelid = i.indexrelid " +
                    "where " +
                    "  psui.schemaname = 'public'::text and " +
                    "  not i.indisunique and " +
                    "  i.indexrelid not in (select * from forein_key_indexes) and " + // retains indexes on FK
                    "  psui.idx_scan < ?::integer and " +
                    "  pg_relation_size(psui.relid) >= ?::integer * 8192 and " + // skips small tables
                    "  pg_relation_size(psui.indexrelid) >= ?::integer * 8192 " + // skip small indexes
                    "order by psui.relname, pg_relation_size(i.indexrelid) desc";

    private static final String FOREIGN_KEYS_WITHOUT_INDEX =
            "select c.conrelid::regclass as table_name, " +
                    "       string_agg(col.attname, ', ' order by u.attposition) as columns, " +
                    "       c.conname as constraint_name, pg_get_constraintdef(c.oid) as definition " +
                    "from pg_constraint c " +
                    "  join lateral unnest(c.conkey) with ordinality as u(attnum, attposition) on true " +
                    "  join pg_class t on (c.conrelid = t.oid) " +
                    "  join pg_attribute col on (col.attrelid = t.oid and col.attnum = u.attnum) " +
                    "where contype = 'f' " +
                    "  and not exists ( " +
                    "    select 1 from pg_index " +
                    "      where indrelid = c.conrelid and " +
                    "        (c.conkey::int[] <@ indkey::int[]) and " + // все поля внешнего ключа должны быть в индексе
                    // порядок полей во внешнем ключе и в индексе совпадает (здесь бы нужно проверить порядок следования
                    // всех полей, но нам это не нужно, так как у нас нет составных FK)
                    "        array_position(indkey::int[], (c.conkey::int[])[1]) = 0 " +
                    "  ) " +
                    "group by c.conrelid, c.conname, c.oid " +
                    "order by (c.conrelid::regclass)::text, columns;";

    private static final String TABLES_WITH_MISSING_INDEXES =
            "with tables_without_indexes as ( " +
                    "  select " +
                    "    relname as table_name, " +
                    "    coalesce(seq_scan, 0) - coalesce(idx_scan, 0) as too_much_seq, " +
                    "    pg_relation_size(relname::regclass) as table_size, " +
                    "    coalesce(seq_scan, 0) as seq_scan, " +
                    "    coalesce(idx_scan, 0) as idx_scan " +
                    "  from pg_stat_all_tables " +
                    "  where " +
                    "      schemaname = 'public' and " +
                    "      pg_relation_size(relname::regclass) > ?::integer * 8192 and " + // skip small tables
                    "      relname not in ('databasechangelog') " +
                    ") " +
                    "select table_name, seq_scan, idx_scan " +
                    "from tables_without_indexes " +
                    "where " +
                    "    (seq_scan + idx_scan) > 100 and " + // table in use
                    "    too_much_seq > 0 " + // too much sequential scans
                    "order by too_much_seq desc;";

    private static final String TABLES_WITHOUT_PRIMARY_KEYS =
            "select tablename as table_name " +
                    "from pg_tables " +
                    "where " +
                    "      schemaname = 'public' and " +
                    "      tablename not in ( " +
                    "          select c.conrelid::regclass::text as table_name " +
                    "          from pg_constraint c " +
                    "          where contype = 'p') and " +
                    "      tablename not in ('databasechangelog') " +
                    "order by tablename;";

    private static final String INDEXES_WITH_NULL_VALUES =
            "select x.indrelid::regclass as table_name, " +
                    "       x.indexrelid::regclass as index_name, " +
                    "       string_agg(a.attname, ', ') as nullable_field, " +
                    "       pg_relation_size(x.indexrelid) as index_size " +
                    "from " +
                    "     pg_index x " +
                    "     join pg_stat_all_indexes psai " +
                    "         on x.indexrelid = psai.indexrelid and psai.schemaname = 'public'::text " +
                    "     join pg_attribute a ON a.attrelid = x.indrelid AND a.attnum = any(x.indkey) " +
                    "where " +
                    "      not x.indisunique and " +
                    "      not a.attnotnull and " +
                    "      array_position(x.indkey, a.attnum) = 0 and " + // only for first segment
                    "      (x.indpred is null or " +
                    "          (position(lower(a.attname) in lower(pg_get_expr(x.indpred, x.indrelid))) = 0)) " +
                    "group by x.indrelid, x.indexrelid, x.indpred " +
                    "order by 1,2";

    private final DataSource dataSource;

    public IndexMaintenanceImpl(@Nonnull DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Nonnull
    @Override
    public List<Index> getInvalidIndexes() {
        final List<Index> invalidIndexes = new ArrayList<>();
        executeQuery(INVALID_INDEXES_SQL, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                final String indexName = rs.getString("index_name");
                    invalidIndexes.add(Index.of(tableName, indexName));
            }
        });
        return invalidIndexes;
    }

    @Nonnull
    @Override
    public List<DuplicatedIndexes> getDuplicatedIndexes() {
        final List<DuplicatedIndexes> duplicatedIndexes = new ArrayList<>();
        executeQuery(DUPLICATED_INDEXES_SQL, rs -> {
            while (rs.next()) {
                final String tableName = rs.getString("table_name");
                final String duplicatedAsString = rs.getString("duplicated_indexes");
                duplicatedIndexes.add(DuplicatedIndexes.of(tableName, duplicatedAsString));
            }
        });
        return duplicatedIndexes;
    }

    @Nonnull
    @Override
    public List<DuplicatedIndexes> getIntersectedIndexes() {
        return null;
    }

    @Nonnull
    @Override
    public List<UnusedIndex> getPotentiallyUnusedIndexes() {
        return null;
    }

    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return null;
    }

    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndexes() {
        return null;
    }

    @Nonnull
    @Override
    public List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey() {
        return null;
    }

    @Nonnull
    @Override
    public List<IndexWithNulls> getIndexesWithNullValues() {
        return null;
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

    @FunctionalInterface
    private interface ResultSetExtractor {

        void extractData(ResultSet rs) throws SQLException;
    }
}
