/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.HostAware;
import com.mfvanek.pg.model.DuplicatedIndices;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.TableWithoutPrimaryKey;
import com.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.List;

public interface IndexMaintenance extends HostAware {

    /**
     * List of invalid (broken) indices to be deleted or re-indexed.
     */
    @Nonnull
    List<Index> getInvalidIndices();

    /**
     * List of duplicated (completely identical) indices (candidates for deletion).
     */
    @Nonnull
    List<DuplicatedIndices> getDuplicatedIndices();

    /**
     * List of intersecting indices (partially identical, candidates for deletion).
     */
    @Nonnull
    List<DuplicatedIndices> getIntersectedIndices();

    /**
     * List of potentially unused indices (candidates for deletion).
     */
    @Nonnull
    List<UnusedIndex> getPotentiallyUnusedIndices();

    /**
     * List of foreign keys without associated indices (potential performance degradation).
     */
    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex();

    /**
     * List of tables with potentially missing indices (potential performance degradation).
     */
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndices();

    /**
     * List of tables without primary key.
     */
    @Nonnull
    List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey();

    /**
     * List of indices that contain null values.
     */
    @Nonnull
    List<IndexWithNulls> getIndicesWithNullValues();
}
