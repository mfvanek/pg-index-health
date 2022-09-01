/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import io.github.mfvanek.pg.model.MemoryUnit;
import org.apache.commons.dbcp2.BasicDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * This wrapper provides postgres containers as part of HA cluster with repmgr configured.
 * <p>
 * If master goes down, repmgr will ensure any of the standby nodes takes the primary role.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
final class PostgresSqlClusterWrapper {

    private static final String IMAGE_NAME = "docker.io/bitnami/postgresql-repmgr";
    private static final String IMAGE_TAG = "14";
    private static final String PRIMARY_ALIAS = "pg-0";
    private static final String STANDBY_ALIAS = "pg-1";

    private final Network network;
    private final JdbcDatabaseContainer<?> containerForPrimary;
    private final JdbcDatabaseContainer<?> containerForStandBy;

    private final DataSource dataSourceForPrimary;
    private final DataSource dataSourceForStandBy;

    PostgresSqlClusterWrapper() {
        this.network = Network.newNetwork();
        final WaitStrategy waitStrategy = new LogMessageWaitStrategy()
                .withRegEx(".*server started.*\\s")
                .withStartupTimeout(Duration.ofSeconds(30));

        // Primary node
        this.containerForPrimary = createContainerAndInitWith(this::primaryEnvVarsMap, PRIMARY_ALIAS, waitStrategy);
        // Standby node
        this.containerForStandBy = createContainerAndInitWith(this::standbyEnvVarsMap, STANDBY_ALIAS, waitStrategy);

        this.containerForPrimary.start();
        this.containerForStandBy.start();

        this.dataSourceForPrimary = buildPrimaryDatasource();
        this.dataSourceForStandBy = buildStandByDatasource();
    }

    @Nonnull
    public DataSource getDataSourceForPrimary() {
        throwErrorIfNotInitialized();
        return dataSourceForPrimary;
    }

    @Nonnull
    public DataSource getDataSourceForStandBy() {
        throwErrorIfNotInitialized();
        return dataSourceForStandBy;
    }

    @Nonnull
    public String getFirstContainerJdbcUrl() {
        throwErrorIfNotInitialized();
        return String.format("jdbc:postgresql://%s:%d/%s", PRIMARY_ALIAS, containerForPrimary.getFirstMappedPort(), containerForPrimary.getDatabaseName());
    }

    @Nonnull
    public String getSecondContainerJdbcUrl() {
        throwErrorIfNotInitialized();
        return String.format("jdbc:postgresql://%s:%d/%s", STANDBY_ALIAS, containerForStandBy.getFirstMappedPort(), containerForStandBy.getDatabaseName());
    }

    public void stopFirstContainer() {
        containerForPrimary.stop();
    }

    @Nonnull
    private Map<String, String> primaryEnvVarsMap() {
        final Map<String, String> envVarsMap = new HashMap<>();
        envVarsMap.put("POSTGRESQL_POSTGRES_PASSWORD", "adminpassword");
        envVarsMap.put("POSTGRESQL_USERNAME", "customuser");
        envVarsMap.put("POSTGRESQL_PASSWORD", "custompassword");
        envVarsMap.put("POSTGRESQL_DATABASE", "customdatabase");
        envVarsMap.put("REPMGR_PASSWORD", "repmgrpassword");
        envVarsMap.put("REPMGR_PRIMARY_HOST", PRIMARY_ALIAS);
        envVarsMap.put("REPMGR_PRIMARY_PORT", "5432");
        envVarsMap.put("REPMGR_PARTNER_NODES", String.format("%s,%s:5432", PRIMARY_ALIAS, STANDBY_ALIAS));
        envVarsMap.put("REPMGR_NODE_NAME", PRIMARY_ALIAS);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", PRIMARY_ALIAS);
        envVarsMap.put("REPMGR_PORT_NUMBER", "5432");
        return envVarsMap;
    }

    @Nonnull
    private Map<String, String> standbyEnvVarsMap() {
        final Map<String, String> envVarsMap = new HashMap<>();
        envVarsMap.put("POSTGRESQL_POSTGRES_PASSWORD", "adminpassword");
        envVarsMap.put("POSTGRESQL_USERNAME", "customuser");
        envVarsMap.put("POSTGRESQL_PASSWORD", "custompassword");
        envVarsMap.put("POSTGRESQL_DATABASE", "customdatabase");
        envVarsMap.put("REPMGR_PASSWORD", "repmgrpassword");
        envVarsMap.put("REPMGR_PRIMARY_HOST", PRIMARY_ALIAS);
        envVarsMap.put("REPMGR_PRIMARY_PORT", "5432");
        envVarsMap.put("REPMGR_PARTNER_NODES", String.format("%s,%s:5432", PRIMARY_ALIAS, STANDBY_ALIAS));
        envVarsMap.put("REPMGR_NODE_NAME", STANDBY_ALIAS);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", STANDBY_ALIAS);
        envVarsMap.put("REPMGR_PORT_NUMBER", "5432");
        return envVarsMap;
    }

    @Nonnull
    private BasicDataSource buildPrimaryDatasource() {
        final BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(containerForPrimary.getJdbcUrl());
        basicDataSource.setUsername(containerForPrimary.getUsername());
        basicDataSource.setPassword(containerForPrimary.getPassword());
        basicDataSource.setDriverClassName(containerForPrimary.getDriverClassName());
        return basicDataSource;
    }

    @Nonnull
    private BasicDataSource buildStandByDatasource() {
        final BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(containerForStandBy.getJdbcUrl());
        basicDataSource.setUsername(containerForStandBy.getUsername());
        basicDataSource.setPassword(containerForStandBy.getPassword());
        basicDataSource.setDriverClassName(containerForStandBy.getDriverClassName());
        return basicDataSource;
    }

    @Nonnull
    private PostgresBitnamiRepmgrContainer createContainerAndInitWith(
            final Supplier<Map<String, String>> envVarsProvider,
            final String alias,
            final WaitStrategy waitStrategy
    ) {
        final PostgresBitnamiRepmgrContainer container = new PostgresBitnamiRepmgrContainer(DockerImageName.parse(IMAGE_NAME).withTag(IMAGE_TAG), envVarsProvider.get());
        container.withCreateContainerCmdModifier(cmd -> cmd.withName(alias));
        container.setNetwork(network);
        container.setWaitStrategy(waitStrategy);
        container.setExposedPorts(Collections.singletonList(5432));
        container.setNetworkAliases(Collections.singletonList(alias));
        container.withSharedMemorySize(MemoryUnit.MB.convertToBytes(512));
        container.withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));
        return container;
    }

    private void throwErrorIfNotInitialized() {
        if (containerForPrimary == null || dataSourceForPrimary == null || containerForStandBy == null || dataSourceForStandBy == null) {
            throw new AssertionError("not initialized");
        }
    }
}
