/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class DiagnosticTest {

    @Test
    void sqlQueryFileNameShouldBeUnique() {
        final Set<String> fileNames = new HashSet<>();
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            fileNames.add(diagnostic.getSqlQueryFileName());
        }
        assertThat(fileNames).hasSize(Diagnostic.values().length);
    }

    @Test
    void sqlQueryFileNameShouldBeInLowerCase() {
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            final String lower = diagnostic.getSqlQueryFileName().toLowerCase(Locale.ROOT);
            assertThat(diagnostic.getSqlQueryFileName()).isEqualTo(lower);
        }
    }

    @Test
    void sqlQueryFileNameShouldHaveSqlExtension() {
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            assertThat(diagnostic.getSqlQueryFileName()).endsWith(".sql");
        }
    }

    @Test
    void checkTypeMustBeSet() {
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            assertThat(diagnostic.isStatic() || diagnostic.isRuntime()).isTrue();
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
        assertThat(countOfChecksAcrossTheCluster).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldBeAtLeastFiveRuntimeChecks() {
        final long countOfRuntimeChecks = Arrays.stream(Diagnostic.values())
            .filter(d -> d.isRuntime() && !d.isStatic())
            .count();
        assertThat(countOfRuntimeChecks).isGreaterThanOrEqualTo(5);
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
}
