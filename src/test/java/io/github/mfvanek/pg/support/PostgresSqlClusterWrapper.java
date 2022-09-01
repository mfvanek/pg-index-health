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
 * This wrapper provide two postgres containers as HA cluster with repmgr configured.
 * Primary and secondary nodes would be named pg-0, pg-1 correspondingly.
 * <p>
 * If master goes down, repmgr will ensure any of the standby nodes takes the primary role.
 *
 * @author Alexey Antipin
 */

final class PostgresSqlClusterWrapper {

    private static final String IMAGE_NAME = "docker.io/bitnami/postgresql-repmgr";
    private static final String IMAGE_TAG = "14";
    private static final String PRIMARY_ALIAS = "pg-0";
    private static final String STANDBY_ALIAS = "pg-1";

    private final Network network;
    private final JdbcDatabaseContainer<?> containerOne;
    private final JdbcDatabaseContainer<?> containerTwo;

    private final DataSource dataSourceOne;
    private final DataSource dataSourceTwo;

    PostgresSqlClusterWrapper() {
        this.network = Network.newNetwork();
        final WaitStrategy waitStrategy = new LogMessageWaitStrategy()
                .withRegEx(".*server started.*\\s")
                .withStartupTimeout(Duration.ofSeconds(30));

        // Primary node
        this.containerOne = createContainerAndInitWith(this::primaryEnvVarsMap, PRIMARY_ALIAS, waitStrategy);
        // Standby node
        this.containerTwo = createContainerAndInitWith(this::standbyEnvVarsMap, STANDBY_ALIAS, waitStrategy);

        this.containerOne.start();
        this.containerTwo.start();

        this.dataSourceOne = buildPrimaryDatasource();
        this.dataSourceTwo = buildStandByDatasource();
    }

    @Nonnull
    public DataSource getDataSourceOne() {
        throwErrorIfNotInitialized();
        return dataSourceOne;
    }

    @Nonnull
    public DataSource getDataSourceTwo() {
        throwErrorIfNotInitialized();
        return dataSourceTwo;
    }

    @Nonnull
    public String getFirstContainerJdbcUrl() {
        throwErrorIfNotInitialized();
        return String.format("jdbc:postgresql://%s:%d/%s", PRIMARY_ALIAS, containerOne.getFirstMappedPort(), containerOne.getDatabaseName());
    }

    @Nonnull
    public String getSecondContainerJdbcUrl() {
        throwErrorIfNotInitialized();
        return String.format("jdbc:postgresql://%s:%d/%s", STANDBY_ALIAS, containerTwo.getFirstMappedPort(), containerTwo.getDatabaseName());
    }

    public void stopFirstContainer() {
        containerOne.stop();
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
        basicDataSource.setUrl(containerOne.getJdbcUrl());
        basicDataSource.setUsername(containerOne.getUsername());
        basicDataSource.setPassword(containerOne.getPassword());
        basicDataSource.setDriverClassName(containerOne.getDriverClassName());
        return basicDataSource;
    }

    @Nonnull
    private BasicDataSource buildStandByDatasource() {
        final BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(containerTwo.getJdbcUrl());
        basicDataSource.setUsername(containerTwo.getUsername());
        basicDataSource.setPassword(containerTwo.getPassword());
        basicDataSource.setDriverClassName(containerTwo.getDriverClassName());
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
        if (containerOne == null || dataSourceOne == null || containerTwo == null || dataSourceTwo == null) {
            throw new AssertionError("not initialized");
        }
    }

}

