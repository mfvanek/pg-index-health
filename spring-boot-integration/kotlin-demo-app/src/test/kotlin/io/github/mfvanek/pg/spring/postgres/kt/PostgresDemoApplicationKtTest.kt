/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt

import com.zaxxer.hikari.HikariDataSource
import io.github.mfvanek.pg.connection.PgConnection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class PostgresDemoApplicationKtTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var environment: Environment

    @Test
    fun contextLoadsAndContainsPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("dataSource"))
            .isInstanceOf(HikariDataSource::class.java)

        assertThat(applicationContext.getBean("pgConnection"))
            .isInstanceOf(PgConnection::class.java)

        assertThat(environment.getProperty("spring.datasource.url"))
            .isNotBlank()
            .startsWith("jdbc:postgresql://localhost:")
            .endsWith("/demo_for_pg_index_health_starter?loggerLevel=OFF")
    }
}
