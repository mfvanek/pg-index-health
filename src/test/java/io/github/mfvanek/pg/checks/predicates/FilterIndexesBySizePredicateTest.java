/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.index.IndexWithSize;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FilterIndexesBySizePredicateTest {

    private static final IndexWithSize FIRST = IndexWithSize.of("t", "idx1", 10L);
    private static final IndexWithSize SECOND = IndexWithSize.of("t", "idx2", 20L);
    private static final IndexWithSize THIRD = IndexWithSize.of("t", "idx3", 30L);

    @Test
    void shouldValidateArguments() {
        assertThatThrownBy(() -> FilterIndexesBySizePredicate.of(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("thresholdInBytes cannot be less than zero");
    }

    @Test
    void shouldBeInclusive() {
        final Predicate<IndexSizeAware> predicate = FilterIndexesBySizePredicate.of(20L);
        assertThat(predicate)
                .rejects(FIRST)
                .accepts(SECOND)
                .accepts(THIRD);
    }

    @Test
    void forZero() {
        final Predicate<IndexSizeAware> predicate = FilterIndexesBySizePredicate.of(0L);
        assertThat(predicate)
                .accepts(FIRST)
                .accepts(SECOND)
                .accepts(THIRD);
    }
}
