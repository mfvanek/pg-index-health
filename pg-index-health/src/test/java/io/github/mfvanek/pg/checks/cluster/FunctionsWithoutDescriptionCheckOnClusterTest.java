/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.function.StoredFunction;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionsWithoutDescriptionCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<StoredFunction> check = new FunctionsWithoutDescriptionCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(StoredFunction.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.FUNCTIONS_WITHOUT_DESCRIPTION);
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withFunctions, ctx -> {
            assertThat(check.check(ctx))
                .hasSize(2)
                .containsExactly(
                    StoredFunction.of(ctx.enrichWithSchema("add"), "a integer, b integer"),
                    StoredFunction.of(ctx.enrichWithSchema("add"), "a integer, b integer, c integer"));

            assertThat(check.check(ctx, f -> !f.getFunctionName().contains("add")))
                .isEmpty();
        });
    }

    @DisabledIf("isProceduresNotSupported")
    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThemForProcedures(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withProcedures, ctx -> {
            assertThat(check.check(ctx))
                .hasSize(2)
                .containsExactly(
                    StoredFunction.of(ctx.enrichWithSchema("insert_data"),
                        isOutParametersInProcedureSupported() ? "IN a integer, IN b integer" : "a integer, b integer"),
                    StoredFunction.of(ctx.enrichWithSchema("insert_data"),
                        isOutParametersInProcedureSupported() ? "IN a integer, IN b integer, IN c integer" : "a integer, b integer, c integer"));

            assertThat(check.check(ctx, f -> !f.getFunctionSignature().contains("c integer")))
                .hasSize(1)
                .containsExactly(
                    StoredFunction.of(ctx.enrichWithSchema("insert_data"),
                        isOutParametersInProcedureSupported() ? "IN a integer, IN b integer" : "a integer, b integer"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withFunctions().withBlankCommentOnFunctions(), ctx ->
            assertThat(check.check(ctx))
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
            assertThat(check.check(ctx))
                .isEmpty()
        );
    }

    @DisabledIf("isProceduresNotSupported")
    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldTakingIntoAccountNonBlankCommentsForProcedures(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withProcedures().withCommentOnProcedures(), ctx ->
            assertThat(check.check(ctx))
                .isEmpty()
        );
    }
}
