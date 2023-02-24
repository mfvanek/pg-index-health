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

class PostgreSqlClusterWrapperTest {

    @Test
    void shouldWork() {
        try (PostgreSqlClusterWrapper cluster = new PostgreSqlClusterWrapper()) {
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
        try (PostgreSqlClusterWrapper cluster = new PostgreSqlClusterWrapper();
             LogsCaptor logsCaptor = new LogsCaptor(PostgreSqlClusterWrapper.class)) {
            assertThat(cluster.stopFirstContainer())
                    .isTrue();
            assertThat(logsCaptor.getLogs())
                    .hasSizeGreaterThanOrEqualTo(1)
                    .anyMatch(l -> l.getMessage().contains("Waiting for standby will be promoted to primary"));
        }
    }
}
