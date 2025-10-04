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

class ObjectsNotFollowingNamingConventionCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull AnyObject> check = new ObjectsNotFollowingNamingConventionCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(AnyObject.class)
            .hasDiagnostic(Diagnostic.OBJECTS_NOT_FOLLOWING_NAMING_CONVENTION)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withMaterializedView().withBadlyNamedObjects(), ctx -> {
            final AnyObject[] baseExpected = {
                AnyObject.ofType(ctx, "\"bad-table-two-fk-bad-ref-id\"", PgObjectType.CONSTRAINT),
                AnyObject.ofType(ctx, "\"bad-table-two_pkey\"", PgObjectType.CONSTRAINT),
                AnyObject.ofType(ctx, "\"bad-table_pkey\"", PgObjectType.CONSTRAINT),
                AnyObject.ofType(ctx, "\"bad-add\"", PgObjectType.FUNCTION),
                AnyObject.ofType(ctx, "\"bad-table-two_pkey\"", PgObjectType.INDEX),
                AnyObject.ofType(ctx, "\"bad-table_pkey\"", PgObjectType.INDEX),
                AnyObject.ofType(ctx, "\"accounts-materialized-view-with-length-63-1234567890-1234567890\"", PgObjectType.MATERIALIZED_VIEW),
                AnyObject.ofType(ctx, "\"bad-table_bad-id_seq\"", PgObjectType.SEQUENCE),
                AnyObject.ofType(ctx, "\"bad-table\"", PgObjectType.TABLE),
                AnyObject.ofType(ctx, "\"bad-table-two\"", PgObjectType.TABLE)
            };
            final AnyObject[] notNullConstraints = {
                AnyObject.ofType(ctx, "\"bad-table-two_bad-ref-id_not_null\"", PgObjectType.CONSTRAINT),
                AnyObject.ofType(ctx, "\"bad-table_bad-id_not_null\"", PgObjectType.CONSTRAINT)
            };
            final AnyObject[] expected = isNotNullConstraintsSupported() ?
                Stream.concat(Arrays.stream(baseExpected), Arrays.stream(notNullConstraints)).toArray(AnyObject[]::new) :
                baseExpected;

            assertThat(check)
                .executing(ctx)
                .hasSize(isNotNullConstraintsSupported() ? 12 : 10)
                .containsExactlyInAnyOrder(expected);

            assertThat(check)
                .executing(ctx, SkipDbObjectsByNamePredicate.of(ctx, List.of("\"bad-table\"", "\"bad-add\"")))
                .hasSize(isNotNullConstraintsSupported() ? 10 : 8);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withBadlyNamedPartitionedTable, ctx -> {
            final AnyObject[] baseExpected = {
                AnyObject.ofType(ctx, "\"one-partitioned_pkey\"", PgObjectType.CONSTRAINT),
                AnyObject.ofType(ctx, "\"one-default_pkey\"", PgObjectType.INDEX),
                AnyObject.ofType(ctx, "\"one-partitioned_pkey\"", PgObjectType.PARTITIONED_INDEX),
                AnyObject.ofType(ctx, "\"one-partitioned\"", PgObjectType.PARTITIONED_TABLE),
                AnyObject.ofType(ctx, "\"one-partitioned_bad-id_seq\"", PgObjectType.SEQUENCE),
                AnyObject.ofType(ctx, "\"one-default\"", PgObjectType.TABLE)
            };
            final AnyObject[] notNullConstraints = {
                AnyObject.ofType(ctx, "\"one-partitioned_bad-id_not_null\"", PgObjectType.CONSTRAINT)
            };
            final AnyObject[] expected = isNotNullConstraintsSupported() ?
                Stream.concat(Arrays.stream(baseExpected), Arrays.stream(notNullConstraints)).toArray(AnyObject[]::new) :
                baseExpected;

            assertThat(check)
                .executing(ctx)
                .hasSize(isNotNullConstraintsSupported() ? 7 : 6)
                .containsExactlyInAnyOrder(expected);
        });
    }
}
