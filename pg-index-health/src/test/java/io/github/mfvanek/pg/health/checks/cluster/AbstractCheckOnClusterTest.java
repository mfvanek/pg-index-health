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

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import io.github.mfvanek.pg.core.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("checkstyle:AbstractClassName")
class AbstractCheckOnClusterTest extends DatabaseAwareTestBase {

    private final AbstractCheckOnCluster<IndexWithColumns> check = new IndexesWithNullValuesCheckOnCluster(getHaPgConnection());

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
        try (LogsCaptor ignored = new LogsCaptor(AbstractCheckOnCluster.class, Level.FINEST)) {
            executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx ->
                assertThat(check.check()) // executing on default schema
                    .hasSize(1)
                    .containsExactly(
                        IndexWithColumns.ofNullable(PgContext.ofDefault(), "clients", "i_clients_middle_name", "middle_name"))
            );
        }
    }

    static class WrongCheck extends AbstractCheckOnCluster<UnusedIndex> {

        WrongCheck(final HighAvailabilityPgConnection haPgConnection) {
            super(haPgConnection, UnusedIndexesCheckOnHost::new);
        }
    }
}
