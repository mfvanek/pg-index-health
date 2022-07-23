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

import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableSizeAware;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class FilterTablesBySizePredicateTest {

    private static final Table FIRST = Table.of("TABLE1", 11L);
    private static final Table SECOND = Table.of("TABLE2", 12L);
    private static final Table THIRD = Table.of("TABLE3", 13L);

    @Test
    void shouldValidateArguments() {
        assertThatThrownBy(() -> FilterTablesBySizePredicate.of(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("thresholdInBytes cannot be less than zero");
    }

    @Test
    void shouldBeInclusive() {
        final Predicate<TableSizeAware> predicate = FilterTablesBySizePredicate.of(12L);
        assertThat(predicate)
                .rejects(FIRST)
                .accepts(SECOND)
                .accepts(THIRD);
    }
}
