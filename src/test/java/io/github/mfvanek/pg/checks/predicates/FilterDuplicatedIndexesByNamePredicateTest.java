/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.IndexWithSize;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FilterDuplicatedIndexesByNamePredicateTest {

    private static final IndexWithSize FIRST = IndexWithSize.of("t", "idx1", 10L);
    private static final IndexWithSize SECOND = IndexWithSize.of("t", "idx2", 20L);
    private static final IndexWithSize THIRD = IndexWithSize.of("t", "idx3", 30L);

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldValidateArguments() {
        assertThatThrownBy(() -> FilterDuplicatedIndexesByNamePredicate.of((String) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("objectName cannot be null");
        assertThatThrownBy(() -> FilterDuplicatedIndexesByNamePredicate.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("objectName cannot be blank");
        assertThatThrownBy(() -> FilterDuplicatedIndexesByNamePredicate.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("objectName cannot be blank");

        assertThatThrownBy(() -> FilterDuplicatedIndexesByNamePredicate.of((Collection<String>) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("exclusions cannot be null");
    }

    @Test
    void forSingleIndexName() {
        final Predicate<DuplicatedIndexes> predicate = FilterDuplicatedIndexesByNamePredicate.of("idx3");
        assertThat(predicate)
                .accepts(DuplicatedIndexes.of(FIRST, SECOND))
                .rejects(DuplicatedIndexes.of(SECOND, THIRD))
                .rejects(DuplicatedIndexes.of(THIRD, FIRST));
    }

    @Test
    void caseShouldNotMatter() {
        final Predicate<DuplicatedIndexes> predicate = FilterDuplicatedIndexesByNamePredicate.of("IDX3");
        assertThat(predicate)
                .accepts(DuplicatedIndexes.of(FIRST, SECOND))
                .rejects(DuplicatedIndexes.of(SECOND, THIRD))
                .rejects(DuplicatedIndexes.of(THIRD, FIRST));
    }

    @Test
    void forMultipleIndexNames() {
        final Predicate<DuplicatedIndexes> predicate = FilterDuplicatedIndexesByNamePredicate.of(List.of("idx3", "idx4", "idx5"));
        assertThat(predicate)
                .accepts(DuplicatedIndexes.of(FIRST, SECOND))
                .rejects(DuplicatedIndexes.of(SECOND, THIRD))
                .rejects(DuplicatedIndexes.of(THIRD, FIRST));
    }

    @Test
    void forEmpty() {
        final Predicate<DuplicatedIndexes> predicate = FilterDuplicatedIndexesByNamePredicate.of(List.of());
        assertThat(predicate)
                .accepts(DuplicatedIndexes.of(FIRST, SECOND))
                .accepts(DuplicatedIndexes.of(SECOND, THIRD))
                .accepts(DuplicatedIndexes.of(THIRD, FIRST));
    }

    @Test
    void shouldCreateDefensiveCopy() {
        final List<String> exclusions = new ArrayList<>(List.of("idx3", "idx4", "idx5"));
        final Predicate<DuplicatedIndexes> predicate = FilterDuplicatedIndexesByNamePredicate.of(exclusions);

        exclusions.clear();
        assertThat(exclusions)
                .isEmpty();

        assertThat(predicate)
                .accepts(DuplicatedIndexes.of(FIRST, SECOND))
                .rejects(DuplicatedIndexes.of(SECOND, THIRD))
                .rejects(DuplicatedIndexes.of(THIRD, FIRST));
    }
}
