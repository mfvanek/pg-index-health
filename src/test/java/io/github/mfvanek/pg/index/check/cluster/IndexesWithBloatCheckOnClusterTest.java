/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.check.cluster;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheck;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.common.maintenance.predicates.FilterIndexesByBloatPredicate;
import io.github.mfvanek.pg.common.maintenance.predicates.FilterIndexesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.predicates.FilterIndexesBySizePredicate;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class IndexesWithBloatCheckOnClusterTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final DatabaseCheck<IndexWithBloat> check;

    IndexesWithBloatCheckOnClusterTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new IndexesWithBloatCheckOnCluster(
                HighAvailabilityPgConnectionImpl.of(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase())));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(IndexWithBloat.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.BLOATED_INDEXES);
    }

    @Test
    void onEmptyDatabase() {
        assertThat(check.check())
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            assertThat(check.check(ctx))
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            assertThat(existsStatisticsForTable(ctx, "accounts"))
                    .isTrue();

            assertThat(check.check(ctx))
                    .isNotNull()
                    .hasSize(3)
                    .containsExactlyInAnyOrder(
                            IndexWithBloat.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_account_number_key"), 0L, 0L, 0),
                            IndexWithBloat.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_pkey"), 0L, 0L, 0),
                            IndexWithBloat.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("clients_pkey"), 0L, 0L, 0))
                    .allMatch(i -> i.getIndexSizeInBytes() > 1L)
                    .allMatch(i -> i.getBloatSizeInBytes() > 1L && i.getBloatPercentage() >= 14);

            final Predicate<IndexSizeAware> predicate = FilterIndexesBySizePredicate.of(1L)
                    .and(FilterIndexesByNamePredicate.of(ctx.enrichWithSchema("accounts_pkey")));
            assertThat(check.check(ctx, predicate))
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            IndexWithBloat.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_account_number_key"), 0L, 0L, 0),
                            IndexWithBloat.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("clients_pkey"), 0L, 0L, 0))
                    .allMatch(i -> i.getIndexSizeInBytes() > 1L)
                    .allMatch(i -> i.getBloatSizeInBytes() > 1L && i.getBloatPercentage() >= 14);

            assertThat(check.check(ctx, FilterIndexesByBloatPredicate.of(1_000_000L, 50)))
                    .isNotNull()
                    .isEmpty();
        });
    }
}
