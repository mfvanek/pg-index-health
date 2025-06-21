/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt.custom.ds

import com.zaxxer.hikari.HikariDataSource
import io.github.mfvanek.pg.connection.PgConnection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class PostgresCustomDataSourceDemoApplicationKtTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun contextLoadsAndContainsPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("pgihCustomDataSource"))
            .isInstanceOf(HikariDataSource::class.java)

        assertThat(applicationContext.getBean("pgConnection"))
            .isInstanceOf(PgConnection::class.java)
    }
}
