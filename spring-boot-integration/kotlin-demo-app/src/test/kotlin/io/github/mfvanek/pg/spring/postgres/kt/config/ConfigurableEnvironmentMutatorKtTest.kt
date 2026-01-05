/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.core.env.Environment
import org.springframework.mock.env.MockEnvironment
import org.testcontainers.postgresql.PostgreSQLContainer

class ConfigurableEnvironmentMutatorKtTest {

    private val postgreSQLContainer: PostgreSQLContainer = Mockito.mock(PostgreSQLContainer::class.java)

    @Test
    fun shouldNotAddPropIfExist() {
        val environment = MockEnvironment()
        environment.setProperty(DATASOURCE_URL_PROP_NAME, "url")

        assertThat(addDatasourceUrlIfNeed(postgreSQLContainer, environment))
            .isFalse()
        assertThat(environment.getProperty(DATASOURCE_URL_PROP_NAME)).isEqualTo("url")
    }

    @Test
    fun shouldNotAddPropIfInvalidType() {
        val environment = Mockito.mock(Environment::class.java)
        Mockito.`when`(environment.getProperty(Mockito.anyString())).thenReturn(null)

        assertThat(addDatasourceUrlIfNeed(postgreSQLContainer, environment))
            .isFalse()
    }

    @Test
    fun shouldAddProperty() {
        val environment = MockEnvironment()
        Mockito.`when`(postgreSQLContainer.jdbcUrl).thenReturn("added_url")

        assertThat(addDatasourceUrlIfNeed(postgreSQLContainer, environment))
            .isTrue()
        assertThat(environment.getProperty(DATASOURCE_URL_PROP_NAME)).isEqualTo("added_url")
    }
}
