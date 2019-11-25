/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.model.DuplicatedIndices;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.TableWithoutPrimaryKey;
import com.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class IndicesHealthImpl implements IndicesHealth {

    private final PgConnection pgConnection;

    public IndicesHealthImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection);
    }

    @Nonnull
    @Override
    public List<Index> getInvalidIndices() {
        return null;
    }

    @Nonnull
    @Override
    public List<DuplicatedIndices> getDuplicatedIndices() {
        return null;
    }

    @Nonnull
    @Override
    public List<DuplicatedIndices> getIntersectedIndices() {
        return null;
    }

    @Nonnull
    @Override
    public List<UnusedIndex> getUnusedIndices() {
        return null;
    }

    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return null;
    }

    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndices() {
        return null;
    }

    @Nonnull
    @Override
    public List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey() {
        return null;
    }

    @Nonnull
    @Override
    public List<IndexWithNulls> getIndicesWithNullValues() {
        return null;
    }
}
