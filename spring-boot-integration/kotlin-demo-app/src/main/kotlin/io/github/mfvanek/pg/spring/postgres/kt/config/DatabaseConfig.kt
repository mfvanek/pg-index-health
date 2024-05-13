/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.mfvanek.pg.testing.PostgresVersionHolder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
class DatabaseConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    fun jdbcDatabaseContainer(): JdbcDatabaseContainer<*> {
        val pgVersion = PostgresVersionHolder.forSingleNode().version
        return PostgreSQLContainer(DockerImageName.parse("postgres").withTag(pgVersion))
            .withDatabaseName("demo_for_pg_index_health_starter")
            .withUsername("demo_user")
            .withPassword("myUniquePassword")
            .waitingFor(Wait.forListeningPort())
    }

    @Bean
    fun dataSource(jdbcDatabaseContainer: JdbcDatabaseContainer<*>, environment: Environment): DataSource {
        addDatasourceUrlIfNeed(jdbcDatabaseContainer, environment)
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = jdbcDatabaseContainer.jdbcUrl
        hikariConfig.username = jdbcDatabaseContainer.username
        hikariConfig.password = jdbcDatabaseContainer.password
        return HikariDataSource(hikariConfig)
    }
}
