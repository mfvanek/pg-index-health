/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class InvalidIndexesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<Index> check = new InvalidIndexesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Index.class)
            .hasDiagnostic(Diagnostic.INVALID_INDEXES)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withInvalidIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(Index.of(ctx, "clients", "i_clients_last_name_first_name"))
                .allMatch(i -> i.getIndexSizeInBytes() == 0L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "clients"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipIndexesByNamePredicate.ofName(ctx, "i_clients_last_name_first_name"))
                .isEmpty();
        });
    }
}
