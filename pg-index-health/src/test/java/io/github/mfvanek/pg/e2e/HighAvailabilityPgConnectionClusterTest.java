/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.e2e;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnection;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for HighAvailabilityPgConnection on cluster.
 *
 * @author Alexey Antipin
 * @since 0.7.0
 */
@Tag("cluster")
class HighAvailabilityPgConnectionClusterTest {

    @Test
    void standbyBecomesPrimaryOnPrimaryDownWithPredefinedDelay() {
        try (PgConnectionAwareCluster postgresCluster = new PgConnectionAwareCluster()) {
            final PgConnection firstConnection = postgresCluster.getFirstPgConnection();
            final PgConnection secondConnection = postgresCluster.getSecondPgConnection();
            final List<PgConnection> pgConnections = List.of(firstConnection, secondConnection);
            final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(firstConnection, pgConnections, 5_000L);

            assertThat(haPgConnection.getConnectionToPrimary())
                    .as("First connection is primary")
                    .isEqualTo(firstConnection)
                    .as("Second connection is not primary")
                    .isNotEqualTo(secondConnection);

            postgresCluster.stopFirstContainer();

            Awaitility
                    .await()
                    .atMost(PgConnectionAwareCluster.MAX_WAIT_INTERVAL_SECONDS)
                    .with()
                    .pollInterval(Duration.ofSeconds(2))
                    .until(() -> haPgConnection.getConnectionToPrimary().equals(secondConnection));

            assertThat(haPgConnection.getConnectionToPrimary())
                    .as("Second connection is primary")
                    .isEqualTo(secondConnection)
                    .as("First connection is not primary")
                    .isNotEqualTo(firstConnection);
        }
    }
}
