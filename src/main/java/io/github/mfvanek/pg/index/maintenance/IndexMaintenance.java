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

public interface IndexMaintenance extends HostAware {

    /**
     * List of invalid (broken) indexes to be deleted or re-indexed.
     */
    @Nonnull
    List<Index> getInvalidIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<Index> getInvalidIndexes() {
        return getInvalidIndexes(PgContext.ofPublic());
    }

    /**
     * List of duplicated (completely identical) indexes (candidates for deletion).
     */
    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<DuplicatedIndexes> getDuplicatedIndexes() {
        return getDuplicatedIndexes(PgContext.ofPublic());
    }

    /**
     * List of intersecting indexes (partially identical, candidates for deletion).
     */
    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<DuplicatedIndexes> getIntersectedIndexes() {
        return getIntersectedIndexes(PgContext.ofPublic());
    }

    /**
     * List of potentially unused indexes (candidates for deletion).
     */
    @Nonnull
    List<UnusedIndex> getPotentiallyUnusedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<UnusedIndex> getPotentiallyUnusedIndexes() {
        return getPotentiallyUnusedIndexes(PgContext.ofPublic());
    }

    /**
     * List of foreign keys without associated indexes (potential performance degradation).
     */
    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull PgContext pgContext);

    @Nonnull
    default List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return getForeignKeysNotCoveredWithIndex(PgContext.ofPublic());
    }

    /**
     * List of tables with potentially missing indexes (potential performance degradation).
     */
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<TableWithMissingIndex> getTablesWithMissingIndexes() {
        return getTablesWithMissingIndexes(PgContext.ofPublic());
    }

    /**
     * List of tables without primary key.
     * <p>
     * Tables without primary key might become a huge problem when bloat occurs
     * because pg_repack will not be able to process them.
     */
    @Nonnull
    List<Table> getTablesWithoutPrimaryKey(@Nonnull PgContext pgContext);

    @Nonnull
    default List<Table> getTablesWithoutPrimaryKey() {
        return getTablesWithoutPrimaryKey(PgContext.ofPublic());
    }

    /**
     * List of indexes that contain null values.
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues(@Nonnull PgContext pgContext);

    @Nonnull
    default List<IndexWithNulls> getIndexesWithNullValues() {
        return getIndexesWithNullValues(PgContext.ofPublic());
    }
}
