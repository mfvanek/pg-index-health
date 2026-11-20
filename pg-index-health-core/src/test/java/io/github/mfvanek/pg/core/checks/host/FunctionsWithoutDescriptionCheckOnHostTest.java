/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.function.StoredFunction;
import io.github.mfvanek.pg.model.predicates.SkipDbObjectsByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class FunctionsWithoutDescriptionCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull StoredFunction> check = new FunctionsWithoutDescriptionCheckOnHost(getPgConnection());

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
        executeTestOnDatabase(schemaName, DatabasePopulator::withFunctions, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    StoredFunction.of(ctx, "add", "a integer, b integer"),
                    StoredFunction.of(ctx, "add", "a integer, b integer, c integer"),
                    StoredFunction.of(ctx, "insert_data", "IN a integer, IN b integer"),
                    StoredFunction.of(ctx, "insert_data", "IN a integer, IN b integer, IN c integer")
                );

            assertThat(check)
                .executing(ctx, SkipDbObjectsByNamePredicate.of(ctx, List.of("add", "insert_data")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withBlankCommentOnFunctions, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    StoredFunction.of(ctx, "add", "a integer, b integer"),
                    StoredFunction.of(ctx, "add", "a integer, b integer, c integer"),
                    StoredFunction.of(ctx, "insert_data", "IN a integer, IN b integer"),
                    StoredFunction.of(ctx, "insert_data", "IN a integer, IN b integer, IN c integer")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldTakingIntoAccountNonBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withCommentOnFunctions, ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty()
        );
    }
}
