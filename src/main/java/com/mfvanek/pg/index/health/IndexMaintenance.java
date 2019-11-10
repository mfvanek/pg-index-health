package com.mfvanek.pg.index.health;

import com.mfvanek.pg.model.DuplicatedIndexes;
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
    List<Index> getInvalidIndexes();

    /**
     * Список дублирующихся (полностью идентичных) индексов (кандидаты на удаление).
     */
    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes();

    /**
     * Список пересекающихся по полям индексов (частично идентичных, кандидаты на удаление).
     */
    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes();

    @Nonnull
    List<UnusedIndex> getPotentiallyUnusedIndexes();

    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex();

    /**
     * Список таблиц с потенциально отсутствующими индексами.
     */
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes();

    /**
     * Список таблиц без первичного ключа.
     */
    @Nonnull
    List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey();

    /**
     * Список индексов, содержащих null значения.
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues();
}
