/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt

import com.zaxxer.hikari.HikariDataSource
import io.github.mfvanek.pg.connection.PgConnection
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost
import io.github.mfvanek.pg.core.checks.common.Diagnostic
import io.github.mfvanek.pg.model.dbobject.DbObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
internal class PostgresDemoApplicationKtTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var environment: Environment

    @Autowired
    private lateinit var checks: List<DatabaseCheckOnHost<out DbObject?>>

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

    @Test
    fun checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.entries.toTypedArray())

        checks
            .filter { it.isStatic }
            .forEach {
                assertThat(it.check())
                    .`as`(it.diagnostic.name)
                    .isEmpty()
            }
    }
}
