/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
class DatabaseConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    fun jdbcDatabaseContainer(): JdbcDatabaseContainer<*> {
        return PostgreSQLContainer("postgres:16.2")
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
