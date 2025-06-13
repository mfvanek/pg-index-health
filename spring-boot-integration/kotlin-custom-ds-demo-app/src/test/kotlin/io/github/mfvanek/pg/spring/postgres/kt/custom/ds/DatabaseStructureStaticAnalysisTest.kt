/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt.custom.ds

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost
import io.github.mfvanek.pg.core.checks.common.Diagnostic
import io.github.mfvanek.pg.model.context.PgContext
import io.github.mfvanek.pg.model.dbobject.DbObject
import io.github.mfvanek.pg.model.predicates.SkipLiquibaseTablesPredicate
import io.github.mfvanek.pg.model.table.Table
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class DatabaseStructureStaticAnalysisTest {

    @Autowired
    private lateinit var checks: List<DatabaseCheckOnHost<out DbObject>>

    @Test
    fun checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.entries.toTypedArray())

        checks
            .filter { it.isStatic }
            .forEach { check ->
                val ctx = PgContext.of("custom_ds_schema")
                // Due to the use of spring.liquibase.default-schema, all names are resolved without a schema
                val listAssert = assertThat(check.check(ctx, SkipLiquibaseTablesPredicate.ofDefault()))
                    .`as`(check.diagnostic.name)

                when (check.diagnostic) {
                    Diagnostic.TABLES_NOT_LINKED_TO_OTHERS ->
                        listAssert
                            .hasSize(1)
                            .containsExactly(Table.of("warehouse"))

                    else -> listAssert.isEmpty()
                }
            }
    }
}
