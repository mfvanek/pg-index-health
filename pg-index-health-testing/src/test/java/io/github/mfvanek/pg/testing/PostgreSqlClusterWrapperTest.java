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

import io.github.mfvanek.pg.support.LogsCaptor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostgreSqlClusterWrapperTest {

    @Test
    void shouldWork() {
        try (PostgreSqlClusterWrapper cluster = PostgreSqlClusterWrapper.builder().build()) {

            assertThat(cluster)
                    .isNotNull();
            assertThat(cluster.getDataSourceForPrimary())
                    .isNotNull()
                    .isInstanceOf(BasicDataSource.class);
            assertThat(cluster.getDataSourceForStandBy())
                    .isNotNull()
                    .isInstanceOf(BasicDataSource.class)
                    .isNotEqualTo(cluster.getDataSourceForPrimary());
            assertThat(cluster.getFirstContainerJdbcUrl())
                    .startsWith("jdbc:postgresql://");
            assertThat(cluster.getSecondContainerJdbcUrl())
                    .startsWith("jdbc:postgresql://")
                    .isNotEqualTo(cluster.getFirstContainerJdbcUrl());
        }
    }

    @Test
    void stopFirstContainerShouldWork() {
        try (PostgreSqlClusterWrapper cluster = PostgreSqlClusterWrapper.builder().build();
             LogsCaptor logsCaptor = new LogsCaptor(PostgreSqlClusterWrapper.class)) {
            assertThat(cluster.stopFirstContainer())
                    .isTrue();
            assertThat(logsCaptor.getLogs())
                    .hasSizeGreaterThanOrEqualTo(1)
                    .anyMatch(l -> l.getMessage().contains("Waiting for standby will be promoted to primary"));
        }
    }

    @Test
    void builderWithDefaultFields() {
        try (PostgreSqlClusterWrapper cluster = PostgreSqlClusterWrapper.builder().build()) {
            assertThat(cluster)
                    .satisfies(it -> {
                        assertThat(it.getUsername()).isEqualTo("customuser");
                        assertThat(it.getPassword()).isEqualTo("custompassword");
                    });
        }
    }

    @Test
    void builderWithCustomFields() {
        try (PostgreSqlClusterWrapper cluster = PostgreSqlClusterWrapper.builder()
                .withUsername("user")
                .withPassword("password")
                .build()) {
            assertThat(cluster)
                    .satisfies(it -> {
                        assertThat(it.getUsername()).isEqualTo("user");
                        assertThat(it.getPassword()).isEqualTo("password");
                    });
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void builderWithInvalidArgs() {
        final PostgreSqlClusterWrapper.Builder builder = PostgreSqlClusterWrapper.builder();
        assertThatThrownBy(() -> builder.withUsername(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("username cannot be null");
        assertThatThrownBy(() -> builder.withPassword(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("password cannot be null");
    }
}
