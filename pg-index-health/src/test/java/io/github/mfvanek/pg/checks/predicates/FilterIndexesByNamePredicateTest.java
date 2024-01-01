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

import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexNameAware;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class FilterIndexesByNamePredicateTest {

    private static final Index FIRST = Index.of("t", "IDX1");
    private static final Index SECOND = Index.of("t", "IDX2");
    private static final Index THIRD = Index.of("t", "IDX3");

    @Test
    void caseShouldNotMatter() {
        final Predicate<IndexNameAware> predicate = FilterIndexesByNamePredicate.of("idx3");
        assertThat(predicate)
                .accepts(FIRST)
                .accepts(SECOND)
                .rejects(THIRD);
    }

    @Test
    void forEmpty() {
        final Predicate<IndexNameAware> predicate = FilterIndexesByNamePredicate.of(List.of());
        assertThat(predicate)
                .accepts(FIRST)
                .accepts(SECOND)
                .accepts(THIRD);
    }
}
