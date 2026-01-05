/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.with.custom.user;

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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseStructureStaticAnalysisTest {

    @Autowired
    private List<DatabaseCheckOnHost<? extends @NonNull DbObject>> checks;

    @Test
    void checksShouldWorkForPublicSchema() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c -> {
                assertThat(c.check())
                    .as(c.getName())
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
                    .as(c.getName());

                switch (c.getName()) {
                    case "TABLES_NOT_LINKED_TO_OTHERS" -> listAssert
                        .hasSize(1)
                        .asInstanceOf(list(Table.class))
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("tableSizeInBytes")
                        .containsExactly(Table.of(ctx, "warehouse"))
                        .allMatch(t -> t.getTableSizeInBytes() > 1L);

                    case "PRIMARY_KEYS_WITH_SERIAL_TYPES" -> listAssert
                        .hasSize(1)
                        .asInstanceOf(list(ColumnWithSerialType.class))
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(
                            ColumnWithSerialType.of(ctx, Column.ofNotNull(ctx, "warehouse", "id"), SerialType.BIG_SERIAL, "warehouse_id_seq")
                        );

                    default -> listAssert.isEmpty();
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
                    .as(c.getName());

                switch (c.getName()) {
                    case "TABLES_WITHOUT_DESCRIPTION", "TABLES_NOT_LINKED_TO_OTHERS" -> listAssert
                        .hasSize(1)
                        .asInstanceOf(list(Table.class))
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("tableSizeInBytes")
                        .containsExactly(Table.of(ctx, "additional_table"))
                        .allMatch(t -> t.getTableSizeInBytes() > 1L);

                    case "COLUMNS_WITHOUT_DESCRIPTION" -> listAssert
                        .hasSize(2)
                        .asInstanceOf(list(Column.class))
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(
                            Column.ofNotNull(ctx, "additional_table", "id"),
                            Column.ofNotNull(ctx, "additional_table", "name")
                        );

                    case "PRIMARY_KEYS_WITH_SERIAL_TYPES" -> listAssert
                        .hasSize(1)
                        .asInstanceOf(list(ColumnWithSerialType.class))
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(
                            ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "additional_table", "id"), "additional_table_id_seq")
                        );

                    default -> listAssert.isEmpty();
                }
            });
    }
}
