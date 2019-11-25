/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.model.DuplicatedIndices;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.TableWithoutPrimaryKey;
import com.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.List;

public interface IndicesHealth {

    @Nonnull
    List<Index> getInvalidIndices();

    @Nonnull
    List<DuplicatedIndices> getDuplicatedIndices();

    @Nonnull
    List<DuplicatedIndices> getIntersectedIndices();

    @Nonnull
    List<UnusedIndex> getUnusedIndices();

    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex();

    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndices();

    @Nonnull
    List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey();

    @Nonnull
    List<IndexWithNulls> getIndicesWithNullValues();
}
