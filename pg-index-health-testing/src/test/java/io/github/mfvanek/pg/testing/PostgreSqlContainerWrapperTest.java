/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
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
                });
        }
    }

    @Test
    void withVersionShouldWork() {
        try (PostgreSqlContainerWrapper container = PostgreSqlContainerWrapper.withVersion("16.4")) {
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
