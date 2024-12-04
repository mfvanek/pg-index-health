package io.github.mfvanek.pg.spring.postgres.kt.custom.ds

import com.zaxxer.hikari.HikariDataSource
import io.github.mfvanek.pg.connection.PgConnection
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
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
internal class PostgresCustomDataSourceDemoApplicationKtTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var checks: List<DatabaseCheckOnHost<out DbObject?>>

    @Test
    fun contextLoadsAndContainsPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("pgihCustomDataSource"))
            .isInstanceOf(HikariDataSource::class.java)

        assertThat(applicationContext.getBean("pgConnection"))
            .isInstanceOf(PgConnection::class.java)
    }

    @Test
    fun checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.entries.toTypedArray())

        checks
            .filter { it.isStatic }
            .forEach { check ->
                val ctx = PgContext.of("custom_ds_schema")
                // Due to the use of spring.liquibase.default-schema, all names are resolved without a schema
                val listAssert = assertThat(check.check(ctx, SkipLiquibaseTablesPredicate.ofPublic()))
                    .`as`(check.diagnostic.name)

                when (check.diagnostic) {
                    Diagnostic.TABLES_NOT_LINKED_TO_OTHERS ->
                        listAssert
                            .hasSize(1)
                            .containsExactly(Table.of("warehouse", 0L))

                    else -> listAssert.isEmpty()
                }
            }
    }
}
