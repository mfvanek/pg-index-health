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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
final class PostgreSqlClusterAliasHolder {

    private final String primaryAlias;
    private final String standbyAlias;

    PostgreSqlClusterAliasHolder() {
        // REPMGR_NODE_NAME must end with a number, so aliases must also
        // To avoid a ConflictException when starting the container, aliases must be unique if there is more than one instance of PostgresSqlClusterWrapper
        final UUID uuid = UUID.randomUUID();
        this.primaryAlias = String.format("pg-%s-0", uuid);
        this.standbyAlias = String.format("pg-%s-1", uuid);
    }

    @Nonnull
    String getPrimaryAlias() {
        return primaryAlias;
    }

    @Nonnull
    public String getStandbyAlias() {
        return standbyAlias;
    }

    @Nonnull
    Map<String, String> createPrimaryEnvVarsMap() {
        final Map<String, String> envVarsMap = createCommonEnvVarsMap();
        envVarsMap.put("REPMGR_NODE_NAME", primaryAlias);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", primaryAlias);
        return envVarsMap;
    }

    @Nonnull
    Map<String, String> createStandbyEnvVarsMap() {
        final Map<String, String> envVarsMap = createCommonEnvVarsMap();
        envVarsMap.put("REPMGR_NODE_NAME", standbyAlias);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", standbyAlias);
        return envVarsMap;
    }

    @Nonnull
    private Map<String, String> createCommonEnvVarsMap() {
        final Map<String, String> envVarsMap = new HashMap<>();
        envVarsMap.put("POSTGRESQL_POSTGRES_PASSWORD", "adminpassword");
        envVarsMap.put("POSTGRESQL_USERNAME", "customuser");
        envVarsMap.put("POSTGRESQL_PASSWORD", "custompassword");
        envVarsMap.put("POSTGRESQL_DATABASE", "customdatabase");
        envVarsMap.put("REPMGR_PASSWORD", "repmgrpassword");
        envVarsMap.put("REPMGR_PRIMARY_HOST", primaryAlias);
        envVarsMap.put("REPMGR_PRIMARY_PORT", "5432");
        envVarsMap.put("REPMGR_PARTNER_NODES", String.format("%s,%s:5432", primaryAlias, standbyAlias));
        envVarsMap.put("REPMGR_PORT_NUMBER", "5432");
        envVarsMap.put("REPMGR_CONNECT_TIMEOUT", "1");
        envVarsMap.put("REPMGR_RECONNECT_ATTEMPTS", "1");
        envVarsMap.put("REPMGR_RECONNECT_INTERVAL", "1");
        return envVarsMap;
    }
}
