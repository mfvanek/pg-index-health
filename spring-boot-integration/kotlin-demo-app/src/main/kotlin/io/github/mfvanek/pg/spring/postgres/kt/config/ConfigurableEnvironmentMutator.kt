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

import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer

internal const val DATASOURCE_URL_PROP_NAME: String = "spring.datasource.url"

internal fun addDatasourceUrlIfNeed(jdbcDatabaseContainer: JdbcDatabaseContainer<*>, environment: Environment): Boolean {
    if (environment.getProperty(DATASOURCE_URL_PROP_NAME) == null && environment is ConfigurableEnvironment) {
        val mps = environment.propertySources
        mps.addFirst(
            MapPropertySource(
                "connectionString",
                mapOf(DATASOURCE_URL_PROP_NAME to jdbcDatabaseContainer.jdbcUrl)
            )
        )
        return true
    }
    return false
}
