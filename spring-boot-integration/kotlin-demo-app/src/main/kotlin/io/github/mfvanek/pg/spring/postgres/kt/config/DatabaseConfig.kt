/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
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
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
class DatabaseConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun postgreSQLContainer(): PostgreSQLContainer {
        val pgVersion = PostgresVersionHolder.forSingleNode().version
        return PostgreSQLContainer(DockerImageName.parse("postgres").withTag(pgVersion))
            .withDatabaseName("demo_for_pg_index_health_starter")
            .withUsername("demo_user")
            .withPassword("myUniquePassword")
            .waitingFor(Wait.forListeningPort())
    }

    @Bean
    fun dataSource(postgreSQLContainer: PostgreSQLContainer, environment: Environment): DataSource {
        addDatasourceUrlIfNeed(postgreSQLContainer, environment)
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = postgreSQLContainer.jdbcUrl
        hikariConfig.username = postgreSQLContainer.username
        hikariConfig.password = postgreSQLContainer.password
        return HikariDataSource(hikariConfig)
    }
}
