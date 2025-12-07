/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.with.custom.user.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.mfvanek.pg.testing.PostgresVersionHolder;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class DatabaseConfig {

    @SuppressWarnings({"java:S2095", "java:S1452", "resource"})
    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer postgreSQLContainer() {
        final String pgVersion = PostgresVersionHolder.forSingleNode().getVersion();
        return new PostgreSQLContainer(DockerImageName.parse("postgres").withTag(pgVersion))
            .withDatabaseName("demo_for_pgih_with_custom_user")
            .withUsername("main_user")
            .withPassword("mainUserPassword")
            .withInitScript("init.sql")
            .waitingFor(Wait.forListeningPort());
    }

    @Primary
    @Bean
    public DataSource dataSource(@NonNull final PostgreSQLContainer postgreSQLContainer,
                                 @NonNull final Environment environment,
                                 @Value("${spring.datasource.username}") final String appUserName,
                                 @Value("${spring.datasource.password}") final String appUserPassword) {
        ConfigurableEnvironmentMutator.addDatasourceUrlIfNeed(postgreSQLContainer, environment);
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        hikariConfig.setUsername(appUserName);
        hikariConfig.setPassword(appUserPassword);
        hikariConfig.setMaximumPoolSize(5);
        return new HikariDataSource(hikariConfig);
    }

    @LiquibaseDataSource
    @Bean
    public DataSource liquibaseDataSource(@NonNull final PostgreSQLContainer postgreSQLContainer) {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        hikariConfig.setUsername(postgreSQLContainer.getUsername());
        hikariConfig.setPassword(postgreSQLContainer.getPassword());
        hikariConfig.setSchema("main_schema");
        hikariConfig.setMaximumPoolSize(1);
        return new HikariDataSource(hikariConfig);
    }
}
