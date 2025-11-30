/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class IntersectedIndexesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull DuplicatedIndexes> check = new IntersectedIndexesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(DuplicatedIndexes.class)
            .hasDiagnostic(Diagnostic.INTERSECTED_INDEXES)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("totalSize", "indexes.indexSizeInBytes")
                .containsExactly(
                    DuplicatedIndexes.of(
                        Index.of(ctx, "accounts", "i_accounts_account_number_not_deleted"),
                        Index.of(ctx, "accounts", "i_accounts_number_balance_not_deleted")
                    ),
                    DuplicatedIndexes.of(
                        Index.of(ctx, "clients", "i_clients_last_first"),
                        Index.of(ctx, "clients", "i_clients_last_name")
                    ))
                .allMatch(d -> d.getTotalSize() >= 106_496L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("accounts", "clients")))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipIndexesByNamePredicate.of(ctx, List.of("i_clients_last_first", "i_accounts_number_balance_not_deleted")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldFindHashIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedHashIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("totalSize", "indexes.indexSizeInBytes")
                .containsExactly(
                    DuplicatedIndexes.of(
                        Index.of(ctx, "clients", "i_clients_last_first"),
                        Index.of(ctx, "clients", "i_clients_last_name")))
                .allMatch(d -> d.getTotalSize() >= 106_496L);

            assertThat(check)
                .executing(ctx, SkipIndexesByNamePredicate.ofName(ctx, "i_clients_last_first"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipIndexesByNamePredicate.ofName(ctx, "i_clients_last_name"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void withDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDifferentOpclassIndexes(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withSerialAndForeignKeysInPartitionedTable().withDuplicatedAndIntersectedIndexesInPartitionedTable(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    DuplicatedIndexes.of(
                        Index.of(ctx, "t1", "idx_t1_deleted_duplicate"),
                        Index.of(ctx, "t1", "idx_t1_deleted_entity_id")),
                    DuplicatedIndexes.of(
                        Index.of(ctx, "t1", "idx_t1_deleted_entity_id"),
                        Index.of(ctx, "t1", "idx_t1_deleted"))
                ));
    }
}
