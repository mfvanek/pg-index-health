/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class DiagnosticTest {

    @Test
    void checkTypeMustBeSet() {
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            assertThat(diagnostic.isStatic() || diagnostic.isRuntime())
                .isTrue();
        }
    }

    @Test
    void shouldBeAtLeastTwoChecksAcrossTheCluster() {
        final long countOfChecksAcrossTheCluster = Arrays.stream(Diagnostic.values())
            .peek(d -> {
                assertThat(d.getQueryExecutor()).isNotNull();
                assertThat(d.getExecutionTopology()).isNotNull();
            })
            .filter(Diagnostic::isAcrossCluster)
            .count();
        assertThat(countOfChecksAcrossTheCluster)
            .isEqualTo(2);
    }

    @Test
    void shouldBeAtLeastFiveRuntimeChecks() {
        final long countOfRuntimeChecks = Arrays.stream(Diagnostic.values())
            .filter(d -> d.isRuntime() && !d.isStatic())
            .count();
        assertThat(countOfRuntimeChecks)
            .isEqualTo(5);
    }

    @Test
    void allAcrossClusterChecksShouldBeRuntime() {
        Arrays.stream(Diagnostic.values())
            .filter(Diagnostic::isAcrossCluster)
            .forEach(d -> assertThat(d.isRuntime())
                .isTrue());
    }

    @Test
    void toStringTest() {
        assertThat(Diagnostic.UNUSED_INDEXES)
            .hasToString("UNUSED_INDEXES");
    }

    @Test
    void canReadQueryFromFile() {
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            assertThat(diagnostic.getSqlQuery())
                .isNotNull()
                .startsWith("/*"); // copyright comment
        }
    }
}
