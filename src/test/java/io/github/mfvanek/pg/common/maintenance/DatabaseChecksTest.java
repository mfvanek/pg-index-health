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

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseChecksTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final DatabaseChecks checks;
    private final HighAvailabilityPgConnection haPgConnection;

    DatabaseChecksTest() {
        super(POSTGRES.getTestDatabase());
        this.haPgConnection = HighAvailabilityPgConnectionImpl.of(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
        this.checks = new DatabaseChecks(this.haPgConnection);
    }

    @Test
    void shouldThrowExceptionForInvalidType() {
        assertThatThrownBy(() -> checks.getCheck(Diagnostic.INVALID_INDEXES, Table.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Illegal type: class io.github.mfvanek.pg.model.table.Table");
    }

    @Test
    void forEachDiagnosticShouldExistCheck() {
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            assertThat(checks.getCheck(diagnostic, TableNameAware.class))
                    .isNotNull()
                    .matches(c -> c.getDiagnostic() == diagnostic)
                    .matches(c -> c.check().isEmpty());
        }
    }

    @Test
    void shouldThrowExceptionIfCheckNotFound() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final DatabaseChecks databaseChecks = new DatabaseChecks(haPgConnection);
        final Field field = databaseChecks.getClass().getDeclaredField("checks");
        field.setAccessible(true);
        final Object fieldValue = field.get(databaseChecks);
        final Method clearMethod = fieldValue.getClass().getDeclaredMethod("clear");
        clearMethod.invoke(fieldValue);

        assertThatThrownBy(() -> databaseChecks.getCheck(Diagnostic.INVALID_INDEXES, Index.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Check for diagnostic INVALID_INDEXES not found");
    }
}
