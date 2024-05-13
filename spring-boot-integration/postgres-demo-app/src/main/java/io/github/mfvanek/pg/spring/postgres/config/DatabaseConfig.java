/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.mfvanek.pg.testing.PostgresVersionHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class DatabaseConfig {

    @SuppressWarnings({"java:S2095", "java:S1452", "resource"})
    @Bean(initMethod = "start", destroyMethod = "stop")
    public JdbcDatabaseContainer<?> jdbcDatabaseContainer() {
        final String pgVersion = PostgresVersionHolder.forSingleNode().getVersion();
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag(pgVersion))
            .withDatabaseName("demo_for_pg_index_health_starter")
            .withUsername("demo_user")
            .withPassword("myUniquePassword")
            .waitingFor(Wait.forListeningPort());
    }

    @Bean
    public DataSource dataSource(@Nonnull final JdbcDatabaseContainer<?> jdbcDatabaseContainer,
                                 @Nonnull final Environment environment) {
        ConfigurableEnvironmentMutator.addDatasourceUrlIfNeed(jdbcDatabaseContainer, environment);
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcDatabaseContainer.getJdbcUrl());
        hikariConfig.setUsername(jdbcDatabaseContainer.getUsername());
        hikariConfig.setPassword(jdbcDatabaseContainer.getPassword());
        return new HikariDataSource(hikariConfig);
    }
}
