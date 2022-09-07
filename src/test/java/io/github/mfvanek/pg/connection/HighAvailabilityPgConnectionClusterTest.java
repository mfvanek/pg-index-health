/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.support.ClusterAwareTestBase;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for HighAvailabilityPgConnection on cluster.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
class HighAvailabilityPgConnectionClusterTest extends ClusterAwareTestBase {

    @Test
    void standbyBecomesPrimaryOnPrimaryDown() {
        final PgConnection firstConnection = getFirstPgConnection();
        final PgConnection secondConnection = getSecondPgConnection();

        final ArrayList<PgConnection> pgConnections = new ArrayList<>();
        pgConnections.add(firstConnection);
        pgConnections.add(secondConnection);

        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(firstConnection, pgConnections, 5);

        assertThat(haPgConnection.getConnectionToPrimary())
                .as("First connection is primary")
                .isEqualTo(firstConnection);
        assertThat(haPgConnection.getConnectionToPrimary())
                .as("Second connection is not primary")
                .isNotEqualTo(secondConnection);

        stopFirstContainer();

        Awaitility
                .await()
                .atMost(Duration.ofSeconds(120))
                .with()
                .pollInterval(Duration.ofSeconds(2))
                .until(() -> haPgConnection.getConnectionToPrimary().equals(secondConnection));

        assertThat(haPgConnection.getConnectionToPrimary())
                .as("Second connection is primary")
                .isEqualTo(secondConnection);
        assertThat(haPgConnection.getConnectionToPrimary())
                .as("First connection is not primary")
                .isNotEqualTo(firstConnection);

        // TODO backward switch is not testable as on start container will get new port that is different from used in PgHostImpl.
        //  Probably caching and reusing firstMapped port in cluster extension is the solution.
    }
}
