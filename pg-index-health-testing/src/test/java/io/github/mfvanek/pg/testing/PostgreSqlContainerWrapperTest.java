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

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostgreSqlContainerWrapperTest {

    @Test
    void withDefaultVersionShouldWork() {
        try (PostgreSqlContainerWrapper container = PostgreSqlContainerWrapper.withDefaultVersion()) {
            assertThat(container)
                .isNotNull()
                .satisfies(c -> {
                    assertThat(c.getDataSource())
                        .isNotNull()
                        .isInstanceOf(BasicDataSource.class);
                    assertThat(c.getPort())
                        .isPositive();
                    assertThat(c.getUrl())
                        .startsWith("jdbc:postgresql://");
                    assertThat(c.getUsername())
                        .isNotBlank();
                    assertThat(c.getPassword())
                        .isNotBlank();
                    assertThat(c.getMountVolume())
                        .isEqualTo(c.isNotNullConstraintsSupported() ? "/var/lib/postgresql/18/docker" : "/var/lib/postgresql/data");
                });
        }
    }

    @Test
    void withNewVersionShouldWork() {
        try (PostgreSqlContainerWrapper container = PostgreSqlContainerWrapper.withVersion("18.1")) {
            assertThat(container)
                .isNotNull()
                .satisfies(c -> {
                    assertThat(c.getDataSource())
                        .isNotNull()
                        .isInstanceOf(BasicDataSource.class);
                    assertThat(c.getPort())
                        .isPositive();
                    assertThat(c.getUrl())
                        .startsWith("jdbc:postgresql://");
                    assertThat(c.getUsername())
                        .isNotBlank();
                    assertThat(c.getPassword())
                        .isNotBlank();
                    assertThat(c.isProceduresSupported())
                        .isTrue();
                    assertThat(c.isOutParametersInProcedureSupported())
                        .isTrue();
                    assertThat(c.isCumulativeStatisticsSystemSupported())
                        .isTrue();
                    assertThat(c.getMountVolume())
                        .isEqualTo("/var/lib/postgresql/18/docker");
                    assertThat(c.isNotNullConstraintsSupported())
                        .isTrue();
                });
        }
    }

    @Test
    void withOldVersionShouldWork() {
        try (PostgreSqlContainerWrapper container = PostgreSqlContainerWrapper.withVersion("17.6")) {
            assertThat(container)
                .isNotNull()
                .satisfies(c -> {
                    assertThat(c.getDataSource())
                        .isNotNull()
                        .isInstanceOf(BasicDataSource.class);
                    assertThat(c.getPort())
                        .isPositive();
                    assertThat(c.getUrl())
                        .startsWith("jdbc:postgresql://");
                    assertThat(c.getUsername())
                        .isNotBlank();
                    assertThat(c.getPassword())
                        .isNotBlank();
                    assertThat(c.isProceduresSupported())
                        .isTrue();
                    assertThat(c.isOutParametersInProcedureSupported())
                        .isTrue();
                    assertThat(c.isCumulativeStatisticsSystemSupported())
                        .isTrue();
                    assertThat(c.getMountVolume())
                        .isEqualTo("/var/lib/postgresql/data");
                    assertThat(c.isNotNullConstraintsSupported())
                        .isFalse();
                });
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenVersionIsNull() {
        assertThatThrownBy(() -> PostgreSqlContainerWrapper.withVersion(null)) //NOSONAR
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgVersion cannot be null");
    }
}
