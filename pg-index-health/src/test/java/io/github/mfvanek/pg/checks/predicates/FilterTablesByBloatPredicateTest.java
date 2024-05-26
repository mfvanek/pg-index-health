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

import io.github.mfvanek.pg.model.table.TableBloatAware;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FilterTablesByBloatPredicateTest {

    @Test
    void shouldValidateArguments() {
        assertThatThrownBy(() -> FilterTablesByBloatPredicate.of(-1L, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sizeThresholdInBytes cannot be less than zero");
        assertThatThrownBy(() -> FilterTablesByBloatPredicate.of(1L, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("percentageThreshold should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> FilterTablesByBloatPredicate.of(1L, 101))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("percentageThreshold should be in the range from 0.0 to 100.0 inclusive");
    }

    @Test
    void shouldBeInclusive() {
        final Predicate<TableBloatAware> predicate = FilterTablesByBloatPredicate.of(10L, 20);
        assertThat(predicate)
            .rejects(TableWithBloat.of("t", 100L, 10L, 10))
            .rejects(TableWithBloat.of("t", 18L, 9L, 50))
            .accepts(TableWithBloat.of("t", 100L, 20L, 20));
    }
}
