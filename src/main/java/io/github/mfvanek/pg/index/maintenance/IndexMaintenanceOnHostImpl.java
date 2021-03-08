/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.common.maintenance.AbstractMaintenance;
import io.github.mfvanek.pg.common.maintenance.Diagnostics;
import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of {@code IndexMaintenance} which collects information from the current host in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 * @see PgHost
 */
public class IndexMaintenanceOnHostImpl extends AbstractMaintenance implements IndexesMaintenanceOnHost {

    public IndexMaintenanceOnHostImpl(@Nonnull final PgConnection pgConnection) {
        super(pgConnection);
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
    public List<UnusedIndex> getUnusedIndexes(@Nonnull final PgContext pgContext) {
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
}
