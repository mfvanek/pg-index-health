/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.index.IndexBloatAware;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FilterIndexesByBloatPredicateTest {

    @Test
    void shouldValidateArguments() {
        assertThatThrownBy(() -> FilterIndexesByBloatPredicate.of(-1L, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sizeThresholdInBytes cannot be less than zero");
        assertThatThrownBy(() -> FilterIndexesByBloatPredicate.of(1L, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("percentageThreshold should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> FilterIndexesByBloatPredicate.of(1L, 101))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("percentageThreshold should be in the range from 0.0 to 100.0 inclusive");
    }

    @Test
    void shouldBeInclusive() {
        final Predicate<IndexBloatAware> predicate = FilterIndexesByBloatPredicate.of(10L, 20);
        assertThat(predicate)
            .rejects(IndexWithBloat.of("t", "idx", 200L, 20L, 10))
            .rejects(IndexWithBloat.of("t", "idx", 18L, 9L, 50))
            .accepts(IndexWithBloat.of("t", "idx", 20L, 10L, 50));
    }

    @Test
    void forZero() {
        final Predicate<IndexBloatAware> predicate = FilterIndexesByBloatPredicate.of(0L, 0);
        assertThat(predicate)
            .accepts(IndexWithBloat.of("t", "idx", 1L, 0L, 0))
            .accepts(IndexWithBloat.of("t", "idx", 11L, 11L, 100));
    }
}
