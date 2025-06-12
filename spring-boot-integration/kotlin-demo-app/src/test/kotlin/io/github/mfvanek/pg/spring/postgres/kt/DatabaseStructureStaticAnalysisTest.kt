/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost
import io.github.mfvanek.pg.core.checks.common.Diagnostic
import io.github.mfvanek.pg.model.dbobject.DbObject
import io.github.mfvanek.pg.model.predicates.SkipByColumnNamePredicate
import io.github.mfvanek.pg.model.predicates.SkipFlywayTablesPredicate
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate
import io.github.mfvanek.pg.model.predicates.SkipLiquibaseTablesPredicate
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate
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

        // Predicates allow you to customize the list of permanent exceptions for your project.
        // Just filter out the database objects that you definitely don't want to see in the check results.
        // Do not use predicates for temporary exceptions or recording deviations in the database structure.
        val exclusions = SkipLiquibaseTablesPredicate.ofDefault()
            .and(SkipFlywayTablesPredicate.ofDefault()) // if you use Flyway
            .and(SkipTablesByNamePredicate.ofName("my_awesome_table"))
            .and(SkipIndexesByNamePredicate.ofName("my_awesome_index"))
            .and(SkipByColumnNamePredicate.ofName("id"))

        checks
            .filter { it.isStatic }
            .forEach {
                assertThat(it.check(exclusions))
                    .`as`(it.diagnostic.name)
                    .isEmpty()
            }
    }
}
