/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import io.github.mfvanek.pg.model.units.MemoryUnit;
import org.awaitility.Awaitility;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresBitnamiRepmgrContainerTest {

    @Test
    void containerShouldWork() {
        final PostgreSqlClusterAliasHolder aliasHolder = new PostgreSqlClusterAliasHolder();
        try (PostgresBitnamiRepmgrContainer container = new PostgresBitnamiRepmgrContainer(
            prepareDockerImageName(), aliasHolder.createPrimaryEnvVarsMap(PostgreSqlClusterWrapper.builder()))
            .withCreateContainerCmdModifier(cmd -> cmd.withName(aliasHolder.getPrimaryAlias()))
            .withSharedMemorySize(MemoryUnit.MB.convertToBytes(768))
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
            .withNetwork(Network.newNetwork())
            .withNetworkAliases(aliasHolder.getPrimaryAlias())
            .withExposedPorts(5432)
            .waitingFor(aliasHolder.getWaitStrategyForPrimary())) {
            container.start();
            Awaitility.await("Ensure container is ready")
                .atMost(PostgreSqlClusterAliasHolder.STARTUP_TIMEOUT)
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> container.getLogs().contains("database system is ready to accept connections"));

            assertThat(container)
                .isNotNull()
                .satisfies(c -> {
                    assertThat(c.getTestQueryString()).isEqualTo("SELECT 1");
                    assertThat(c.getDriverClassName()).isEqualTo("org.postgresql.Driver");
                    assertThat(c.getJdbcUrl()).startsWith("jdbc:postgresql://");
                });
        }
    }

    @Test
    void testEqualsAndHashCode() {
        final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder = PostgreSqlClusterWrapper.builder();
        final PostgreSqlClusterAliasHolder aliasHolder = new PostgreSqlClusterAliasHolder();
        try (PostgresBitnamiRepmgrContainer first = new PostgresBitnamiRepmgrContainer(prepareDockerImageName(), aliasHolder.createPrimaryEnvVarsMap(builder));
             PostgresBitnamiRepmgrContainer second = new PostgresBitnamiRepmgrContainer(prepareDockerImageName(), aliasHolder.createStandbyEnvVarsMap(builder))) {
            assertThat(first)
                .isNotNull()
                .isNotEqualTo(null)
                .isEqualTo(first)
                .isNotEqualTo(BigDecimal.ONE)
                .doesNotHaveSameHashCodeAs(aliasHolder.createPrimaryEnvVarsMap(builder))
                .isNotEqualTo(second)
                .doesNotHaveSameHashCodeAs(second);
        }
    }

    @NonNull
    private DockerImageName prepareDockerImageName() {
        return DockerImageName.parse("docker.io/bitnamilegacy/postgresql-repmgr")
            .withTag("17.6.0");
    }
}
