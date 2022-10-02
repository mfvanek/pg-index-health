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
import org.graalvm.compiler.core.common.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for HighAvailabilityPgConnection on cluster.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HighAvailabilityPgConnectionClusterTest {

    private ClusterAwareTestBase postgresCluster;

    @BeforeEach
    protected void initCluster() {
        this.postgresCluster = new ClusterAwareTestBase();
    }

    @Test
    @SuppressFBWarnings(
            value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
            justification = "False positive"
    )
    void standbyBecomesPrimaryOnPrimaryDownWithPredefinedDelay() {
        final PgConnection firstConnection = postgresCluster.getFirstPgConnection();
        final PgConnection secondConnection = postgresCluster.getSecondPgConnection();
        final List<PgConnection> pgConnections = Arrays.asList(firstConnection, secondConnection);
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(firstConnection, pgConnections, 5_000L);

        assertThat(haPgConnection.getConnectionToPrimary())
                .as("First connection is primary")
                .isEqualTo(firstConnection)
                .as("Second connection is not primary")
                .isNotEqualTo(secondConnection);

        postgresCluster.stopFirstContainer();

        Awaitility
                .await()
                .atMost(Duration.ofSeconds(120))
                .with()
                .pollInterval(Duration.ofSeconds(2))
                .until(() -> haPgConnection.getConnectionToPrimary().equals(secondConnection));

        assertThat(haPgConnection.getConnectionToPrimary())
                .as("Second connection is primary")
                .isEqualTo(secondConnection)
                .as("First connection is not primary")
                .isNotEqualTo(firstConnection);
    }

    @Test
    @SuppressFBWarnings(
            value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
            justification = "False positive"
    )
    void standbyBecomesPrimaryOnPrimaryDownWithDefaultDelay() {
        final PgConnection firstConnection = postgresCluster.getFirstPgConnection();
        final PgConnection secondConnection = postgresCluster.getSecondPgConnection();
        final List<PgConnection> pgConnections = Arrays.asList(firstConnection, secondConnection);
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(firstConnection, pgConnections);

        assertThat(haPgConnection.getConnectionToPrimary())
                .as("First connection is primary")
                .isEqualTo(firstConnection)
                .as("Second connection is not primary")
                .isNotEqualTo(secondConnection);

        postgresCluster.stopFirstContainer();

        Awaitility
                .await()
                .atMost(Duration.ofSeconds(120))
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
