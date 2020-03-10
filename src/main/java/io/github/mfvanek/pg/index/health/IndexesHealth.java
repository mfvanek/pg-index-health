/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health;

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

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An entry point for collecting and managing statistics about the health of tables and indexes on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see PgContext
 */
public interface IndexesHealth {

    @Nonnull
    List<Index> getInvalidIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<Index> getInvalidIndexes() {
        return getInvalidIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<DuplicatedIndexes> getDuplicatedIndexes() {
        return getDuplicatedIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<DuplicatedIndexes> getIntersectedIndexes() {
        return getIntersectedIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<UnusedIndex> getUnusedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<UnusedIndex> getUnusedIndexes() {
        return getUnusedIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull PgContext pgContext);

    @Nonnull
    default List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return getForeignKeysNotCoveredWithIndex(PgContext.ofPublic());
    }

    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<TableWithMissingIndex> getTablesWithMissingIndexes() {
        return getTablesWithMissingIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<Table> getTablesWithoutPrimaryKey(@Nonnull PgContext pgContext);

    @Nonnull
    default List<Table> getTablesWithoutPrimaryKey() {
        return getTablesWithoutPrimaryKey(PgContext.ofPublic());
    }

    /**
     * Returns indexes in the specified schema on master host that contain null values.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of indexes with null values
     * @see PgContext
     * @see IndexWithNulls
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues(@Nonnull PgContext pgContext);

    /**
     * Returns indexes in the public schema on master host that contain null values.
     *
     * @return list of indexes with null values
     * @see PgContext
     * @see IndexWithNulls
     */
    @Nonnull
    default List<IndexWithNulls> getIndexesWithNullValues() {
        return getIndexesWithNullValues(PgContext.ofPublic());
    }

    /**
     * Returns bloated indexes in the specified schema on master host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated indexes
     * @see PgContext
     * @see IndexWithBloat
     */
    @Nonnull
    List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext);

    /**
     * Returns bloated indexes in the public schema on master host.
     *
     * @return list of bloated indexes
     * @see PgContext
     * @see IndexWithBloat
     */
    @Nonnull
    default List<IndexWithBloat> getIndexesWithBloat() {
        return getIndexesWithBloat(PgContext.ofPublic());
    }

    /**
     * Returns bloated tables in the specified schema on master host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated tables
     * @see PgContext
     * @see TableWithBloat
     */
    @Nonnull
    List<TableWithBloat> getTablesWithBloat(@Nonnull PgContext pgContext);

    /**
     * Returns bloated tables in the public schema on master host.
     *
     * @return list of bloated tables
     * @see PgContext
     * @see TableWithBloat
     */
    @Nonnull
    default List<TableWithBloat> getTablesWithBloat() {
        return getTablesWithBloat(PgContext.ofPublic());
    }

    /**
     * Reset all statistics counters on all hosts in the cluster to zero.
     * <p>
     * It is safe running this method on your database.
     * It just reset counters without any impact on performance.
     *
     * @see io.github.mfvanek.pg.index.maintenance.StatisticsMaintenance
     */
    void resetStatistics();
}
