/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health;

import io.github.mfvanek.pg.index.IndexesHealthAware;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import io.github.mfvanek.pg.table.TablesHealthAware;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * An entry point for collecting and managing statistics
 * about the health of tables and indexes on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see PgContext
 */
public interface DatabaseHealth extends IndexesHealthAware, TablesHealthAware {

    /**
     * Returns invalid (broken) indexes on the primary host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of invalid indexes
     * @see Index
     */
    @Override
    @Nonnull
    List<Index> getInvalidIndexes(@Nonnull PgContext pgContext);

    @Override
    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull PgContext pgContext);

    @Override
    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull PgContext pgContext);

    @Override
    @Nonnull
    List<UnusedIndex> getUnusedIndexes(@Nonnull PgContext pgContext);

    @Override
    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull PgContext pgContext);

    /**
     * Returns indexes in the specified schema on primary host that contain null values.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of indexes with null values
     * @see PgContext
     * @see IndexWithNulls
     */
    @Override
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues(@Nonnull PgContext pgContext);

    /**
     * Returns bloated indexes in the specified schema on primary host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated indexes
     * @see PgContext
     * @see IndexWithBloat
     */
    @Override
    @Nonnull
    List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext);

    @Override
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull PgContext pgContext);

    @Override
    @Nonnull
    List<Table> getTablesWithoutPrimaryKey(@Nonnull PgContext pgContext);

    /**
     * Returns bloated tables in the specified schema on primary host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated tables
     * @see PgContext
     * @see TableWithBloat
     */
    @Override
    @Nonnull
    List<TableWithBloat> getTablesWithBloat(@Nonnull PgContext pgContext);
}
