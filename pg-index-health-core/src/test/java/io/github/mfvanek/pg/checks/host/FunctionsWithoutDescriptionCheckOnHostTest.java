/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.function.StoredFunction;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class FunctionsWithoutDescriptionCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<StoredFunction> check = new FunctionsWithoutDescriptionCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(StoredFunction.class)
            .hasDiagnostic(Diagnostic.FUNCTIONS_WITHOUT_DESCRIPTION)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withFunctions, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    StoredFunction.of(ctx.enrichWithSchema("add"), "a integer, b integer"),
                    StoredFunction.of(ctx.enrichWithSchema("add"), "a integer, b integer, c integer")
                ));
    }

    @DisabledIf("isProceduresNotSupported")
    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThemForProcedures(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withProcedures, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    StoredFunction.of(ctx.enrichWithSchema("insert_data"),
                        isOutParametersInProcedureSupported() ? "IN a integer, IN b integer" : "a integer, b integer"),
                    StoredFunction.of(ctx.enrichWithSchema("insert_data"),
                        isOutParametersInProcedureSupported() ? "IN a integer, IN b integer, IN c integer" : "a integer, b integer, c integer")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withFunctions().withBlankCommentOnFunctions(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    StoredFunction.of(ctx.enrichWithSchema("add"), "a integer, b integer"),
                    StoredFunction.of(ctx.enrichWithSchema("add"), "a integer, b integer, c integer")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldTakingIntoAccountNonBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withFunctions().withCommentOnFunctions(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty()
        );
    }

    @DisabledIf("isProceduresNotSupported")
    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldTakingIntoAccountNonBlankCommentsForProcedures(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withProcedures().withCommentOnProcedures(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty()
        );
    }
}
