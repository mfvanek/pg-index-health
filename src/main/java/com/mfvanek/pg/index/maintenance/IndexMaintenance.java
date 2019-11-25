/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.model.DuplicatedIndices;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.TableWithoutPrimaryKey;
import com.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.List;

public interface IndexMaintenance {

    /**
     * Список невалидных (битых) индексов, которые нужно удалить или переиндексировать.
     */
    @Nonnull
    List<Index> getInvalidIndices();

    /**
     * Список дублирующихся (полностью идентичных) индексов (кандидаты на удаление).
     */
    @Nonnull
    List<DuplicatedIndices> getDuplicatedIndices();

    /**
     * Список пересекающихся по полям индексов (частично идентичных, кандидаты на удаление).
     */
    @Nonnull
    List<DuplicatedIndices> getIntersectedIndices();

    @Nonnull
    List<UnusedIndex> getPotentiallyUnusedIndices();

    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex();

    /**
     * Список таблиц с потенциально отсутствующими индексами.
     */
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndices();

    /**
     * Список таблиц без первичного ключа.
     */
    @Nonnull
    List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey();

    /**
     * Список индексов, содержащих null значения.
     */
    @Nonnull
    List<IndexWithNulls> getIndicesWithNullValues();
}
