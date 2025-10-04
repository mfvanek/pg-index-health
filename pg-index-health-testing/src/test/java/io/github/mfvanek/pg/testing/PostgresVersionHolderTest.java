/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
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

    @SuppressWarnings("DataFlowIssue")
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
                assertThat(v.isNotNullConstraintsSupported()).isFalse();
            });
    }

    @Test
    void notNullConstraintsNotSupported() {
        assertThat(new PostgresVersionHolder("15.1"))
            .isNotNull()
            .satisfies(v -> {
                assertThat(v.getVersion()).isEqualTo("15.1");
                assertThat(v.isOutParametersInProcedureSupported()).isTrue();
                assertThat(v.isProceduresSupported()).isTrue();
                assertThat(v.isCumulativeStatisticsSystemSupported()).isTrue();
                assertThat(v.isNotNullConstraintsSupported()).isFalse();
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
    void notNullConstraintsSupported() {
        assertThat(new PostgresVersionHolder("18.0"))
            .isNotNull()
            .satisfies(v -> {
                assertThat(v.getVersion()).isEqualTo("18.0");
                assertThat(v.isOutParametersInProcedureSupported()).isTrue();
                assertThat(v.isProceduresSupported()).isTrue();
                assertThat(v.isCumulativeStatisticsSystemSupported()).isTrue();
                assertThat(v.isNotNullConstraintsSupported()).isTrue();
            });
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
                .isEqualTo("18.0");
        }
    }

    @Test
    void forSingleNodeShouldBeAbleToForceVersion() {
        final PostgresVersionHolder versionHolder = PostgresVersionHolder.forSingleNode("345.678");
        assertThat(versionHolder)
            .isNotNull();
        assertThat(versionHolder.getVersion())
            .isEqualTo("345.678");
    }
}
