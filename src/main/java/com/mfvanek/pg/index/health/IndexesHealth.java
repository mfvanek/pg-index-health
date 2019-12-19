/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.model.DuplicatedIndexes;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.Table;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.List;

public interface IndexesHealth {

    @Nonnull
    List<Index> getInvalidIndexes();

    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes();

    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes();

    @Nonnull
    List<UnusedIndex> getUnusedIndexes();

    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex();

    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes();

    @Nonnull
    List<Table> getTablesWithoutPrimaryKey();

    /**
     * Get indexes that contain null values from all hosts.
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues();

    /**
     * Reset all statistics counters on all hosts to zero.
     */
    void resetStatistics();
}
