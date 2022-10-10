/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseChecksTest extends DatabaseAwareTestBase {

    private static final String[] SCHEMAS = {PgContext.DEFAULT_SCHEMA_NAME, "custom"};

    private final DatabaseChecks checks = new DatabaseChecks(getHaPgConnection());

    @Test
    void shouldThrowExceptionForInvalidType() {
        assertThatThrownBy(() -> checks.getCheck(Diagnostic.INVALID_INDEXES, Table.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Illegal type: class io.github.mfvanek.pg.model.table.Table");
    }

    @ParameterizedTest
    @EnumSource(Diagnostic.class)
    @DisplayName("For each diagnostic should exist check")
    void completenessTest(@Nonnull final Diagnostic diagnostic) {
        assertThat(checks.getCheck(diagnostic, DbObject.class))
                .isNotNull()
                .satisfies(c -> assertThat(c.getDiagnostic())
                        .isEqualTo(diagnostic));
    }

    @Test
    void shouldThrowExceptionIfCheckNotFound() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final DatabaseChecks databaseChecks = new DatabaseChecks(getHaPgConnection());
        final Field field = databaseChecks.getClass().getDeclaredField("checks");
        field.setAccessible(true);
        final Object fieldValue = field.get(databaseChecks);
        final Method clearMethod = fieldValue.getClass().getDeclaredMethod("clear");
        clearMethod.invoke(fieldValue);

        assertThatThrownBy(() -> databaseChecks.getCheck(Diagnostic.INVALID_INDEXES, Index.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Check for diagnostic INVALID_INDEXES not found");
    }

    @ParameterizedTest
    @EnumSource(Diagnostic.class)
    @DisplayName("Each check should return nothing on empty database")
    void onEmptyDatabaseCheckShouldReturnNothing(@Nonnull final Diagnostic diagnostic) {
        assertThat(checks.getCheck(diagnostic, DbObject.class).check())
                .isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = Diagnostic.class, mode = EnumSource.Mode.EXCLUDE, names = {"BLOATED_INDEXES", "BLOATED_TABLES", "FOREIGN_KEYS_WITHOUT_INDEX"})
    void onDatabaseWithoutThemCheckShouldReturnNothing(@Nonnull final Diagnostic diagnostic) {
        for (final String schemaName : SCHEMAS) {
            executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withCommentOnColumns().withCommentOnTables(), ctx ->
                    assertThat(checks.getCheck(diagnostic, DbObject.class).check(ctx))
                            .isEmpty());
        }
    }
}
