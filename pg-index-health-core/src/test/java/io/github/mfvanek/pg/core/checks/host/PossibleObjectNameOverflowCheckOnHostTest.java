/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.dbobject.AnyObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.predicates.SkipDbObjectsByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class PossibleObjectNameOverflowCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull AnyObject> check = new PossibleObjectNameOverflowCheckOnHost(getPgConnection());

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
            final String matViewName = "\"accounts-materialized-view-with-length-63-1234567890-1234567890\"";
            final String constraintName = "num_less_than_million_constraint_with_length_63_1234567890_1234";
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    AnyObject.ofType(ctx, constraintName, PgObjectType.CONSTRAINT),
                    AnyObject.ofType(ctx, matViewName, PgObjectType.MATERIALIZED_VIEW));

            assertThat(check)
                .executing(ctx, SkipDbObjectsByNamePredicate.of(ctx, List.of(matViewName, constraintName)))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVeryLongNamesInPartitionedTable, ctx -> {
            final AnyObject[] baseExpected = {
                AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_1234567890_12_pkey", PgObjectType.CONSTRAINT),
                AnyObject.ofType(ctx, "entity_default_long_1234567890_1234567890_1234567890_12345_pkey", PgObjectType.INDEX),
                AnyObject.ofType(ctx, "entity_default_long_1234567890_123456789_ref_type_ref_value_idx", PgObjectType.INDEX),
                AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_1234567890_12_pkey", PgObjectType.PARTITIONED_INDEX),
                AnyObject.ofType(ctx, "idx_entity_long_1234567890_1234567890_1234567890_1234567890_123", PgObjectType.PARTITIONED_INDEX),
                AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_1234567890_1234567", PgObjectType.PARTITIONED_TABLE),
                AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_1234_entity_id_seq", PgObjectType.SEQUENCE),
                AnyObject.ofType(ctx, "entity_default_long_1234567890_1234567890_1234567890_1234567890", PgObjectType.TABLE)
            };
            final AnyObject[] notNullConstraints = {
                AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_entity_id_not_null", PgObjectType.CONSTRAINT)
            };
            final AnyObject[] expected = isNotNullConstraintsSupported() ?
                Stream.concat(Arrays.stream(baseExpected), Arrays.stream(notNullConstraints)).toArray(AnyObject[]::new) :
                baseExpected;

            assertThat(check)
                .executing(ctx)
                .hasSize(isNotNullConstraintsSupported() ? 9 : 8)
                .containsExactlyInAnyOrder(expected);
        });
    }
}
