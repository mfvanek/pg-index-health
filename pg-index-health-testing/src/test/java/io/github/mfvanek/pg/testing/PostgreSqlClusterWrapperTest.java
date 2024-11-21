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

import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import io.github.mfvanek.pg.connection.fixtures.support.PostgresVersionReader;
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
            assertThat(cluster.getCommonUrlToPrimary())
                .startsWith("jdbc:postgresql://")
                .containsPattern("^.+/localhost:[0-9]{4,6},localhost:[0-9]{4,6}/.+$")
                .endsWith("/customdatabase?connectTimeout=1&hostRecheckSeconds=2&socketTimeout=600&targetServerType=primary");
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
                    assertThat(it.getFirstContainerJdbcUrl()).contains("/customdatabase");
                    assertThat(it.getSecondContainerJdbcUrl()).contains("/customdatabase");
                });
        }
    }

    @Test
    void builderWithCustomFields() {
        try (PostgreSqlClusterWrapper cluster = PostgreSqlClusterWrapper.builder()
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("test_db_with_repmgr")
            .build()) {
            assertThat(cluster)
                .satisfies(it -> {
                    assertThat(it.getUsername()).isEqualTo("user");
                    assertThat(it.getPassword()).isEqualTo("password");
                    assertThat(it.getFirstContainerJdbcUrl()).contains("/test_db_with_repmgr");
                    assertThat(it.getSecondContainerJdbcUrl()).contains("/test_db_with_repmgr");
                });
        }
    }

    @Test
    void builderWithForcedVersion() {
        final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder = PostgreSqlClusterWrapper.builder()
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("test_db_with_repmgr")
            .withPostgresVersion("14.7");
        assertThat(builder.getPostgresVersion())
            .isEqualTo("14.7");
        try (PostgreSqlClusterWrapper cluster = builder.build()) {
            assertThat(cluster)
                .satisfies(it -> {
                    assertThat(it.getUsername()).isEqualTo("user");
                    assertThat(it.getPassword()).isEqualTo("password");
                    assertThat(it.getFirstContainerJdbcUrl()).contains("/test_db_with_repmgr");
                    assertThat(it.getSecondContainerJdbcUrl()).contains("/test_db_with_repmgr");
                });
            final String actualPgVersionString = PostgresVersionReader.readVersion(cluster.getDataSourceForPrimary());
            assertThat(actualPgVersionString).startsWith("14.7");
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void builderWithInvalidArgs() {
        final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder = PostgreSqlClusterWrapper.builder();
        assertThatThrownBy(() -> builder.withUsername(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("username cannot be null");
        assertThatThrownBy(() -> builder.withPassword(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("password cannot be null");
        assertThatThrownBy(() -> builder.withDatabaseName(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("databaseName cannot be null");
        assertThatThrownBy(() -> builder.withPostgresVersion(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("postgresVersion cannot be null");
    }
}
