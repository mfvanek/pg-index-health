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

import io.github.mfvanek.pg.checks.predicates.FilterIndexesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class IndexesWithBooleanCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<IndexWithColumns> check = new IndexesWithBooleanCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(IndexWithColumns.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.INDEXES_WITH_BOOLEAN);
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBooleanValuesInIndex(), ctx -> {
            assertThat(check.check(ctx))
                .hasSize(1)
                .containsExactly(
                    IndexWithColumns.ofSingle(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_deleted"), 0L,
                        Column.ofNotNull(ctx.enrichWithSchema("accounts"), "deleted")));

            assertThat(check.check(ctx, FilterIndexesByNamePredicate.of(ctx.enrichWithSchema("i_accounts_deleted"))))
                .isEmpty();
        });
    }
}
