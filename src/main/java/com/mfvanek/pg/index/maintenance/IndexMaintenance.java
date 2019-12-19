/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.HostAware;
import com.mfvanek.pg.model.DuplicatedIndexes;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.Table;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.List;

public interface IndexMaintenance extends HostAware {

    /**
     * List of invalid (broken) indexes to be deleted or re-indexed.
     */
    @Nonnull
    List<Index> getInvalidIndexes();

    /**
     * List of duplicated (completely identical) indexes (candidates for deletion).
     */
    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes();

    /**
     * List of intersecting indexes (partially identical, candidates for deletion).
     */
    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes();

    /**
     * List of potentially unused indexes (candidates for deletion).
     */
    @Nonnull
    List<UnusedIndex> getPotentiallyUnusedIndexes();

    /**
     * List of foreign keys without associated indexes (potential performance degradation).
     */
    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex();

    /**
     * List of tables with potentially missing indexes (potential performance degradation).
     */
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes();

    /**
     * List of tables without primary key.
     *
     * Tables without primary key might become a huge problem when bloat occurs
     * because pg_repack will not be able to process them.
     */
    @Nonnull
    List<Table> getTablesWithoutPrimaryKey();

    /**
     * List of indexes that contain null values.
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues();
}
