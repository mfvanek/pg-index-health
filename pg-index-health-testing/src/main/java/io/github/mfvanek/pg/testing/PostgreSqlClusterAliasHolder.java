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

import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * A utility class to manage aliases and environment variables for a PostgreSQL cluster
 * consisting of primary and standby nodes. This class is primarily used for defining
 * unique aliases for nodes within the cluster and configuring environment variables
 * required for the cluster setup.
 */
final class PostgreSqlClusterAliasHolder {

    /**
     * The maximum time to wait for the startup of the clustered PostgreSQL container.
     */
    static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(40L);

    private final String primaryAlias;
    private final String standbyAlias;

    PostgreSqlClusterAliasHolder() {
        // REPMGR_NODE_NAME must end with a number, so aliases must also
        // To avoid a ConflictException when starting the container, aliases must be unique if there is more than one instance of PostgresSqlClusterWrapper
        final UUID uuid = UUID.randomUUID();
        this.primaryAlias = String.format(Locale.ROOT, "pg-%s-0", uuid);
        this.standbyAlias = String.format(Locale.ROOT, "pg-%s-1", uuid);
    }

    String getPrimaryAlias() {
        return primaryAlias;
    }

    String getStandbyAlias() {
        return standbyAlias;
    }

    Map<String, String> createPrimaryEnvVarsMap(
        final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder
    ) {
        final Map<String, String> envVarsMap = createCommonEnvVarsMap(builder);
        envVarsMap.put("REPMGR_NODE_NAME", primaryAlias);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", primaryAlias);
        return envVarsMap;
    }

    Map<String, String> createStandbyEnvVarsMap(
        final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder
    ) {
        final Map<String, String> envVarsMap = createCommonEnvVarsMap(builder);
        envVarsMap.put("REPMGR_NODE_NAME", standbyAlias);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", standbyAlias);
        return envVarsMap;
    }

    WaitStrategy getWaitStrategyForPrimary() {
        return new LogMessageWaitStrategy()
            .withRegEx(".*Starting repmgrd.*\\s")
            .withStartupTimeout(STARTUP_TIMEOUT);
    }

    WaitStrategy getWaitStrategyForStandBy() {
        return new LogMessageWaitStrategy()
            .withRegEx(".*starting monitoring of node.*\\s")
            .withStartupTimeout(STARTUP_TIMEOUT);
    }

    private Map<String, String> createCommonEnvVarsMap(
        final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder
    ) {
        final Map<String, String> envVarsMap = new HashMap<>();
        envVarsMap.put("POSTGRESQL_POSTGRES_PASSWORD", "adminpassword");
        envVarsMap.put("POSTGRESQL_USERNAME", builder.getUsername());
        envVarsMap.put("POSTGRESQL_PASSWORD", builder.getPassword());
        envVarsMap.put("POSTGRESQL_DATABASE", builder.getDatabaseName());
        envVarsMap.put("REPMGR_PASSWORD", "repmgrpassword");
        envVarsMap.put("REPMGR_PRIMARY_HOST", primaryAlias);
        envVarsMap.put("REPMGR_PRIMARY_PORT", "5432");
        envVarsMap.put("REPMGR_PARTNER_NODES", String.format(Locale.ROOT, "%s,%s:5432", primaryAlias, standbyAlias));
        envVarsMap.put("REPMGR_PORT_NUMBER", "5432");
        envVarsMap.put("REPMGR_CONNECT_TIMEOUT", "1");
        envVarsMap.put("REPMGR_RECONNECT_ATTEMPTS", "1");
        envVarsMap.put("REPMGR_RECONNECT_INTERVAL", "1");
        return envVarsMap;
    }
}
