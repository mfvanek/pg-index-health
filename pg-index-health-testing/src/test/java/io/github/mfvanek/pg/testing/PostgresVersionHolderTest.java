/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class PostgresVersionHolderTest {

    @Test
    void shouldThrowExceptionForInvalidVersion() {
        assertThatThrownBy(() -> new PostgresVersionHolder(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("pgVersion cannot be null");
    }

    @Test
    void oldPostgresVersionsSupportNothing() {
        assertThat(new PostgresVersionHolder("9.6.24"))
                .isNotNull()
                .satisfies(v -> {
                    assertThat(v.getVersion()).isEqualTo("9.6.24");
                    assertThat(v.isOutParametersInProcedureSupported()).isFalse();
                    assertThat(v.isProceduresSupported()).isFalse();
                    assertThat(v.isCumulativeStatisticsSystemSupported()).isFalse();
                });
    }

    @Test
    void newPostgresVersionsSupportEverything() {
        assertThat(new PostgresVersionHolder("15.1"))
                .isNotNull()
                .satisfies(v -> {
                    assertThat(v.getVersion()).isEqualTo("15.1");
                    assertThat(v.isOutParametersInProcedureSupported()).isTrue();
                    assertThat(v.isProceduresSupported()).isTrue();
                    assertThat(v.isCumulativeStatisticsSystemSupported()).isTrue();
                });
    }

    @Test
    void outParametersNotSupported() {
        assertThat(new PostgresVersionHolder("13.4.1"))
                .isNotNull()
                .satisfies(v -> {
                    assertThat(v.getVersion()).isEqualTo("13.4.1");
                    assertThat(v.isOutParametersInProcedureSupported()).isFalse();
                    assertThat(v.isProceduresSupported()).isTrue();
                    assertThat(v.isCumulativeStatisticsSystemSupported()).isFalse();
                });
    }

    @Test
    void forClusterShouldBeBitnamiAware() {
        final PostgresVersionHolder versionHolder = PostgresVersionHolder.forCluster();
        assertThat(versionHolder)
                .isNotNull()
                .satisfies(v -> {
                    assertThat(v.getVersion()).endsWith(".0");
                    assertThat(v.getVersion().chars()
                            .mapToObj(c -> (char) c)
                            .filter(c -> c == '.'))
                            .hasSize(2);
                });
        if (System.getenv("TEST_PG_VERSION") != null) {
            assertThat(versionHolder.getVersion())
                    .isEqualTo(System.getenv("TEST_PG_VERSION") + ".0");
        } else {
            assertThat(versionHolder.getVersion())
                    .isEqualTo("15.2.0");
        }
    }

    @Test
    void forSingleNodeShouldBeEnvAware() {
        final PostgresVersionHolder versionHolder = PostgresVersionHolder.forSingleNode();
        assertThat(versionHolder)
                .isNotNull();
        if (System.getenv("TEST_PG_VERSION") != null) {
            assertThat(versionHolder.getVersion())
                    .isEqualTo(System.getenv("TEST_PG_VERSION"));
        } else {
            assertThat(versionHolder.getVersion())
                    .isEqualTo("15.2");
        }
    }
}
