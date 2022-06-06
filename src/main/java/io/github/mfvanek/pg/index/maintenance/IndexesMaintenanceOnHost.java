/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.index.IndexesHealthAware;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * A set of diagnostics for collecting statistics about the health of indexes on a specific host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 * @see PgContext
 * @see IndexesHealthAware
 */
public interface IndexesMaintenanceOnHost extends IndexesHealthAware, HostAware {

    /**
     * Returns invalid (broken) indexes on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of invalid indexes
     * @see Index
     */
    @Override
    @Nonnull
    List<Index> getInvalidIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns duplicated (completely identical) indexes in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of duplicated indexes
     */
    @Override
    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns intersected indexes (partially identical) on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of intersected indexes
     */
    @Override
    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns unused indexes on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of unused indexes
     */
    @Override
    @Nonnull
    List<UnusedIndex> getUnusedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns foreign keys without associated indexes on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of foreign keys without associated indexes
     */
    @Override
    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull PgContext pgContext);

    /**
     * Returns indexes that contain null values on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of indexes with null values
     */
    @Override
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues(@Nonnull PgContext pgContext);

    /**
     * Returns indexes that are bloated on the current host in the specified schema.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated indexes
     */
    @Override
    @Nonnull
    List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext);
}
