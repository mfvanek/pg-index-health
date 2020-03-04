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
import io.github.mfvanek.pg.model.TableWithBloat;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import io.github.mfvanek.pg.utils.QueryExecutor;
import io.github.mfvanek.pg.utils.ResultSetExtractor;
import io.github.mfvanek.pg.utils.SqlQueryReader;

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
        return executeQuery(Diagnostics.INVALID_INDEXES, pgContext, rs -> {
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
                Diagnostics.DUPLICATED_INDEXES, pgContext, "duplicated_indexes");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull final PgContext pgContext) {
        return getDuplicatedOrIntersectedIndexes(
                Diagnostics.INTERSECTED_INDEXES, pgContext, "intersected_indexes");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<UnusedIndex> getPotentiallyUnusedIndexes(@Nonnull final PgContext pgContext) {
        return executeQuery(Diagnostics.UNUSED_INDEXES, pgContext, rs -> {
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
        return executeQuery(Diagnostics.FOREIGN_KEYS_WITHOUT_INDEX, pgContext, rs -> {
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
        return executeQuery(Diagnostics.TABLES_WITH_MISSING_INDEXES, pgContext, rs -> {
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
        return executeQuery(Diagnostics.TABLES_WITHOUT_PRIMARY_KEY, pgContext, rs -> {
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
        return executeQuery(Diagnostics.INDEXES_WITH_NULL_VALUES, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final String indexName = rs.getString("index_name");
            final long indexSize = rs.getLong("index_size");
            final String nullableField = rs.getString("nullable_fields");
            return IndexWithNulls.of(tableName, indexName, indexSize, nullableField);
        });
    }

    @Nonnull
    private List<DuplicatedIndexes> getDuplicatedOrIntersectedIndexes(@Nonnull final Diagnostics diagnostics,
                                                                      @Nonnull final PgContext pgContext,
                                                                      @Nonnull final String columnName) {
        return executeQuery(diagnostics, pgContext, rs -> {
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
        return executeQueryWithBloatThreshold(Diagnostics.BLOATED_INDEXES, pgContext, rs -> {
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
    public List<TableWithBloat> getTablesWithBloat(@Nonnull PgContext pgContext) {
        return executeQueryWithBloatThreshold(Diagnostics.BLOATED_TABLES, pgContext, rs -> {
            final String tableName = rs.getString("table_name");
            final long tableSize = rs.getLong("table_size");
            final long bloatSize = rs.getLong("bloat_size");
            final int bloatPercentage = rs.getInt("bloat_percentage");
            return TableWithBloat.of(tableName, tableSize, bloatSize, bloatPercentage);
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

    private <T> List<T> executeQuery(@Nonnull final Diagnostics diagnostics,
                                     @Nonnull final PgContext pgContext,
                                     @Nonnull final ResultSetExtractor<T> rse) {
        final String sqlQuery = SqlQueryReader.getQueryFromFile(diagnostics.getSqlQueryFileName());
        return QueryExecutor.executeQueryWithSchema(pgConnection, pgContext, sqlQuery, rse);
    }

    private <T> List<T> executeQueryWithBloatThreshold(@Nonnull final Diagnostics diagnostics,
                                                       @Nonnull final PgContext pgContext,
                                                       @Nonnull final ResultSetExtractor<T> rse) {
        final String sqlQuery = SqlQueryReader.getQueryFromFile(diagnostics.getSqlQueryFileName());
        return QueryExecutor.executeQueryWithBloatThreshold(pgConnection, pgContext, sqlQuery, rse);
    }
}
