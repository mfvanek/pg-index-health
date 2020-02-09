/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A set of diagnostics for collecting statistics about the health of tables and indexes on a specific host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 * @see PgContext
 */
public interface IndexMaintenance extends HostAware {

    /**
     * Returns invalid (broken) indexes to be deleted or re-indexed on current host with the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    List<Index> getInvalidIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns invalid (broken) indexes to be deleted or re-indexed on current host in the public schema.
     *
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    default List<Index> getInvalidIndexes() {
        return getInvalidIndexes(PgContext.ofPublic());
    }

    /**
     * Returns duplicated (completely identical) indexes (candidates for deletion) on current host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of duplicated indexes
     */
    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<DuplicatedIndexes> getDuplicatedIndexes() {
        return getDuplicatedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns intersected indexes (partially identical, candidates for deletion) on current host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of intersected indexes
     */
    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<DuplicatedIndexes> getIntersectedIndexes() {
        return getIntersectedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns potentially unused indexes (candidates for deletion) on current host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of potentially unused indexes
     */
    @Nonnull
    List<UnusedIndex> getPotentiallyUnusedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<UnusedIndex> getPotentiallyUnusedIndexes() {
        return getPotentiallyUnusedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns foreign keys without associated indexes (potential performance degradation) on current host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of foreign keys without associated indexes
     */
    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull PgContext pgContext);

    @Nonnull
    default List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return getForeignKeysNotCoveredWithIndex(PgContext.ofPublic());
    }

    /**
     * Returns tables with potentially missing indexes (potential performance degradation) on current host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of tables with potentially missing indexes
     */
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<TableWithMissingIndex> getTablesWithMissingIndexes() {
        return getTablesWithMissingIndexes(PgContext.ofPublic());
    }

    /**
     * Returns tables without primary key on current host.
     * <p>
     * Tables without primary key might become a huge problem when bloat occurs
     * because pg_repack will not be able to process them.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of tables without primary key
     */
    @Nonnull
    List<Table> getTablesWithoutPrimaryKey(@Nonnull PgContext pgContext);

    @Nonnull
    default List<Table> getTablesWithoutPrimaryKey() {
        return getTablesWithoutPrimaryKey(PgContext.ofPublic());
    }

    /**
     * Returns indexes that contain null values on current host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of indexes with null values
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues(@Nonnull PgContext pgContext);

    @Nonnull
    default List<IndexWithNulls> getIndexesWithNullValues() {
        return getIndexesWithNullValues(PgContext.ofPublic());
    }
}
