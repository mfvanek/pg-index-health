/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.utils;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class CollectionUtilsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(CollectionUtils.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void intersectionShouldThrowExceptionOnInvalidArguments() {
        assertThatThrownBy(() -> CollectionUtils.intersection(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("first cannot be null");
        final List<Object> empty = List.of();
        assertThatThrownBy(() -> CollectionUtils.intersection(empty, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("second cannot be null");
    }

    @Test
    void intersectionShouldWorkWithEmptyCollections() {
        assertThat(CollectionUtils.intersection(List.of(), Set.of(1)))
            .isEmpty();
        assertThat(CollectionUtils.intersection(Set.of(1), List.of()))
            .isEmpty();
    }

    @Test
    void intersectionShouldRemoveDuplicates() {
        assertThat(CollectionUtils.intersection(List.of(10, 10, 11, 12), Set.of(10, 12, 14)))
            .hasSize(2)
            .containsExactly(10, 12);
        assertThat(CollectionUtils.intersection(Set.of(44, 90, 33, 12), List.of(21, 44, 12, 33, 34, 12, 44, 33)))
            .hasSize(3)
            .containsExactlyInAnyOrder(44, 33, 12); // ordering based on first argument and not guaranteed for Set
    }

    @Test
    void intersectionShouldRemainOrderingFromFirstIfPossible() {
        assertThat(CollectionUtils.intersection(List.of(7, 1, -1, 0, 3), List.of(0, 3, 7)))
            .hasSize(3)
            .containsExactly(7, 0, 3);
    }
}
