/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("checkstyle:AbstractClassName")
class AbstractCheckOnClusterTest extends DatabaseAwareTestBase {

    private final AbstractCheckOnCluster<IndexWithNulls> check = new IndexesWithNullValuesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldThrowExceptionIfMapperNotPassedForCrossClusterCheck() {
        final HighAvailabilityPgConnection haPgConnection = getHaPgConnection();
        assertThatThrownBy(() -> new WrongCheck(haPgConnection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("acrossClusterResultsMapper cannot be null for diagnostic UNUSED_INDEXES");
    }

    @ParameterizedTest
    @ValueSource(strings = PgContext.DEFAULT_SCHEMA_NAME)
    void forPublicSchema(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx ->
                assertThat(check.check()) // executing on public schema by default
                        .hasSize(1)
                        .containsExactly(
                                IndexWithNulls.of("clients", "i_clients_middle_name", 0L, "middle_name"))
        );
    }

    static class WrongCheck extends AbstractCheckOnCluster<UnusedIndex> {

        WrongCheck(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
            super(haPgConnection, UnusedIndexesCheckOnHost::new);
        }
    }
}
