/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
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
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
final class PostgreSqlClusterAliasHolder {

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

    @Nonnull
    String getPrimaryAlias() {
        return primaryAlias;
    }

    @Nonnull
    String getStandbyAlias() {
        return standbyAlias;
    }

    @Nonnull
    Map<String, String> createPrimaryEnvVarsMap(
        @Nonnull final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder
    ) {
        final Map<String, String> envVarsMap = createCommonEnvVarsMap(builder);
        envVarsMap.put("REPMGR_NODE_NAME", primaryAlias);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", primaryAlias);
        return envVarsMap;
    }

    @Nonnull
    Map<String, String> createStandbyEnvVarsMap(
        @Nonnull final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder
    ) {
        final Map<String, String> envVarsMap = createCommonEnvVarsMap(builder);
        envVarsMap.put("REPMGR_NODE_NAME", standbyAlias);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", standbyAlias);
        return envVarsMap;
    }

    @Nonnull
    WaitStrategy getWaitStrategyForPrimary() {
        return new LogMessageWaitStrategy()
            .withRegEx(".*Starting repmgrd.*\\s")
            .withStartupTimeout(STARTUP_TIMEOUT);
    }

    @Nonnull
    WaitStrategy getWaitStrategyForStandBy() {
        return new LogMessageWaitStrategy()
            .withRegEx(".*starting monitoring of node.*\\s")
            .withStartupTimeout(STARTUP_TIMEOUT);
    }

    @Nonnull
    private Map<String, String> createCommonEnvVarsMap(
        @Nonnull final PostgreSqlClusterWrapper.PostgreSqlClusterBuilder builder
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
