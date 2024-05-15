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

import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class FilterTablesByNamePredicateTest {

    private static final Table FIRST = Table.of("TABLE1", 1L);
    private static final Table SECOND = Table.of("TABLE2", 2L);
    private static final Table THIRD = Table.of("TABLE3", 3L);

    @Test
    void caseShouldNotMatter() {
        final Predicate<TableNameAware> predicate = FilterTablesByNamePredicate.of("table3");
        assertThat(predicate)
            .accepts(FIRST)
            .accepts(SECOND)
            .rejects(THIRD);
    }

    @Test
    void forEmpty() {
        final Predicate<TableNameAware> predicate = FilterTablesByNamePredicate.of(List.of());
        assertThat(predicate)
            .accepts(FIRST)
            .accepts(SECOND)
            .accepts(THIRD);
    }
}
