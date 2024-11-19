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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.AnyObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.predicates.SkipDbObjectsByNamePredicate;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class PossibleObjectNameOverflowCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<AnyObject> check = new PossibleObjectNameOverflowCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(AnyObject.class)
            .hasDiagnostic(Diagnostic.POSSIBLE_OBJECT_NAME_OVERFLOW)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withMaterializedView().withIdentityPrimaryKey(), ctx -> {
            final String matViewName = ctx.enrichWithSchema("accounts_materialized_view_with_length_63_1234567890_1234567890");
            final String constraintName = ctx.enrichWithSchema("num_less_than_million_constraint_with_length_63_1234567890_1234");
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                    AnyObject.ofType(matViewName, PgObjectType.MATERIALIZED_VIEW),
                    AnyObject.ofType(constraintName, PgObjectType.CONSTRAINT));

            assertThat(check)
                .executing(ctx, SkipDbObjectsByNamePredicate.of(List.of(matViewName, constraintName)))
                .isEmpty();
        });
    }
}
