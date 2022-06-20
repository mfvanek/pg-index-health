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

import io.github.mfvanek.pg.model.table.TableBloatAware;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class FilterTablesByBloatPredicateTest {

    @Test
    void shouldBeInclusive() {
        final Predicate<TableBloatAware> predicate = FilterTablesByBloatPredicate.of(10L, 20);
        assertThat(predicate)
                .rejects(TableWithBloat.of("t", 100L, 10L, 10))
                .rejects(TableWithBloat.of("t", 18L, 9L, 50))
                .accepts(TableWithBloat.of("t", 100L, 20L, 20));
    }
}
