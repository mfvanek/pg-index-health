/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.AnyObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.predicates.SkipDbObjectsByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class PossibleObjectNameOverflowCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<AnyObject> check = new PossibleObjectNameOverflowCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(AnyObject.class)
            .hasDiagnostic(Diagnostic.POSSIBLE_OBJECT_NAME_OVERFLOW)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withMaterializedView().withIdentityPrimaryKey(), ctx -> {
            final String matViewName = "accounts_materialized_view_with_length_63_1234567890_1234567890";
            final String constraintName = "num_less_than_million_constraint_with_length_63_1234567890_1234";
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                    AnyObject.ofType(ctx, matViewName, PgObjectType.MATERIALIZED_VIEW),
                    AnyObject.ofType(ctx, constraintName, PgObjectType.CONSTRAINT));

            assertThat(check)
                .executing(ctx, SkipDbObjectsByNamePredicate.of(ctx, List.of(matViewName, constraintName)))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVeryLongNamesInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(9)
                .containsExactly(
                    AnyObject.ofType(ctx, "entity_default_long_1234567890_1234567890_1234567890_12345_pkey", PgObjectType.CONSTRAINT),
                    AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_1234567890_12_pkey", PgObjectType.CONSTRAINT),
                    AnyObject.ofType(ctx, "entity_default_long_1234567890_1234567890_1234567890_12345_pkey", PgObjectType.INDEX),
                    AnyObject.ofType(ctx, "entity_default_long_1234567890_123456789_ref_type_ref_value_idx", PgObjectType.INDEX),
                    AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_1234567890_12_pkey", PgObjectType.PARTITIONED_INDEX),
                    AnyObject.ofType(ctx, "idx_entity_long_1234567890_1234567890_1234567890_1234567890_123", PgObjectType.PARTITIONED_INDEX),
                    AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_1234567890_1234567", PgObjectType.PARTITIONED_TABLE),
                    AnyObject.ofType(ctx, "entity_long_1234567890_1234567890_1234567890_1234_entity_id_seq", PgObjectType.SEQUENCE),
                    AnyObject.ofType(ctx, "entity_default_long_1234567890_1234567890_1234567890_1234567890", PgObjectType.TABLE)
                ));
    }
}
