/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.with.custom.user;

import com.zaxxer.hikari.HikariDataSource;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.predicates.SkipLiquibaseTablesPredicate;
import io.github.mfvanek.pg.model.table.Table;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@ActiveProfiles("test")
@SpringBootTest
class PostgresWithCustomUserDemoApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Autowired
    private List<DatabaseCheckOnHost<? extends DbObject>> checks;

    @Test
    void contextLoadsAndDoesNotContainPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("dataSource"))
            .isInstanceOf(HikariDataSource.class);

        assertThat(applicationContext.getBean("liquibaseDataSource"))
            .isInstanceOf(HikariDataSource.class);

        assertThat(applicationContext.getBean("pgConnection"))
            .isInstanceOf(PgConnection.class);

        assertThat(environment.getProperty("spring.datasource.url"))
            .isNotBlank();

        assertThat(environment.getProperty("spring.liquibase.url"))
            .isNotBlank();
    }

    @Test
    void checksShouldWorkForPublicSchema() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c -> {
                assertThat(c.check())
                    .as(c.getDiagnostic().name())
                    .isEmpty();

                assertThat(c.getHost().getPgUrl())
                    .startsWith("jdbc:postgresql://localhost:")
                    .endsWith("/demo_for_pgih_with_custom_user?loggerLevel=OFF");
            });
    }

    @Test
    void checksShouldWorkForMainSchema() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        final PgContext ctx = PgContext.of("main_schema");
        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c -> {
                final ListAssert<? extends DbObject> listAssert = assertThat(c.check(ctx, SkipLiquibaseTablesPredicate.of(ctx)))
                    .as(c.getDiagnostic().name());

                switch (c.getDiagnostic()) {
                    case TABLES_NOT_LINKED_TO_OTHERS:
                        listAssert
                            .hasSize(1)
                            .asInstanceOf(list(Table.class))
                            .containsExactly(Table.of(ctx, "warehouse"));
                        break;

                    case PRIMARY_KEYS_WITH_SERIAL_TYPES:
                        listAssert
                            .hasSize(1)
                            .asInstanceOf(list(ColumnWithSerialType.class))
                            .containsExactly(ColumnWithSerialType.of(
                                Column.ofNotNull(ctx, "warehouse", "id"),
                                SerialType.BIG_SERIAL, ctx.enrichWithSchema("warehouse_id_seq")));
                        break;

                    default:
                        listAssert.isEmpty();
                        break;
                }
            });
    }

    @Test
    void checksShouldWorkForAdditionalSchema() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        final PgContext ctx = PgContext.of("additional_schema");
        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c -> {
                final ListAssert<? extends DbObject> listAssert = assertThat(c.check(ctx))
                    .as(c.getDiagnostic().name());

                switch (c.getDiagnostic()) {
                    case TABLES_WITHOUT_DESCRIPTION:
                    case TABLES_NOT_LINKED_TO_OTHERS:
                        listAssert.hasSize(1)
                            .asInstanceOf(list(Table.class))
                            .containsExactly(Table.of(ctx, "additional_table"));
                        break;

                    case COLUMNS_WITHOUT_DESCRIPTION:
                        listAssert.hasSize(2);
                        break;

                    case PRIMARY_KEYS_WITH_SERIAL_TYPES:
                        listAssert.hasSize(1)
                            .asInstanceOf(list(ColumnWithSerialType.class))
                            .containsExactly(ColumnWithSerialType.of(
                                Column.ofNotNull(ctx, "additional_table", "id"), SerialType.BIG_SERIAL, ctx.enrichWithSchema("additional_table_id_seq"))
                            );
                        break;

                    default:
                        listAssert.isEmpty();
                        break;
                }
            });
    }
}
