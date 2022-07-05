/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.checks.cluster.IndexesWithNullValuesCheckOnCluster;
import io.github.mfvanek.pg.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractCheckOnClusterTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final HighAvailabilityPgConnection haPgConnection;
    private final AbstractCheckOnCluster<IndexWithNulls> check;

    AbstractCheckOnClusterTest() {
        super(POSTGRES.getTestDatabase());
        this.haPgConnection = HighAvailabilityPgConnectionImpl.of(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
        this.check = new IndexesWithNullValuesCheckOnCluster(haPgConnection);
    }

    @Test
    void shouldThrowExceptionIfMapperNotPassedForCrossClusterCheck() {
        assertThatThrownBy(() -> new WrongCheck(haPgConnection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("acrossClusterResultsMapper cannot be null for diagnostic UNUSED_INDEXES");
    }

    @ParameterizedTest
    @ValueSource(strings = {"public"})
    void forPublicSchema(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx ->
                assertThat(check.check()) // executing on public schema by default
                        .hasSize(1)
                        .containsExactly(
                                IndexWithNulls.of("clients", "i_clients_middle_name", 0L, "middle_name"))
        );
    }

    static class WrongCheck extends AbstractCheckOnCluster<UnusedIndex> {

        public WrongCheck(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
            super(haPgConnection, UnusedIndexesCheckOnHost::new);
        }
    }
}
