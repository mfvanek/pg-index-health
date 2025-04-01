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
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.predicates.SkipDbObjectsByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class UnusedIndexesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<UnusedIndex> check = new UnusedIndexesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(UnusedIndex.class)
            .hasDiagnostic(Diagnostic.UNUSED_INDEXES)
            .hasHost(getHost())
            .isRuntime();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(6)
                .containsExactlyInAnyOrder(
                    UnusedIndex.of(ctx, "clients", "i_clients_last_first"),
                    UnusedIndex.of(ctx, "clients", "i_clients_last_name"),
                    UnusedIndex.of(ctx, "accounts", "i_accounts_account_number"),
                    UnusedIndex.of(ctx, "accounts", "i_accounts_number_balance_not_deleted"),
                    UnusedIndex.of(ctx, "accounts", "i_accounts_account_number_not_deleted"),
                    UnusedIndex.of(ctx, "accounts", "i_accounts_id_account_number_not_deleted"))
                .allMatch(i -> i.getIndexSizeInBytes() > 0L)
                .allMatch(i -> i.getIndexScans() == 0);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts")
                    .and(SkipIndexesByNamePredicate.of(ctx, List.of("i_clients_last_first", "i_clients_last_name"))))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts")
                    .and(SkipDbObjectsByNamePredicate.of(List.of(ctx.enrichWithSchema("i_clients_last_first"), ctx.enrichWithSchema("i_clients_last_name")))))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withNullableIndexesInPartitionedTable, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                    UnusedIndex.of(ctx, "custom_entity_reference_with_very_very_very_long_name_1_default", "custom_entity_reference_with_very_very_v_ref_type_ref_value_idx"),
                    UnusedIndex.of(ctx, "custom_entity_reference_with_very_very_very_long_name_1_default", "custom_entity_reference_with_very_very__entity_id_ref_value_idx"),
                    UnusedIndex.of(ctx, "custom_entity_reference_with_very_very_very_long_name_1_default", "idx_custom_entity_reference_with_very_very_very_long_name_1_d_3"))
                .allMatch(i -> i.getIndexSizeInBytes() > 0L)
                .allMatch(i -> i.getIndexScans() == 0);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "custom_entity_reference_with_very_very_very_long_name_1_default"))
                .isEmpty();
        });
    }
}
