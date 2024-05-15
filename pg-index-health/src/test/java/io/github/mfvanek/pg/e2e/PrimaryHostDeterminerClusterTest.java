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

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PrimaryHostDeterminer;
import io.github.mfvanek.pg.connection.PrimaryHostDeterminerImpl;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Primary host determiner tests on cluster.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
@Tag("cluster")
class PrimaryHostDeterminerClusterTest {

    private final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();

    @Test
    void correctPrimaryDetection() {
        try (PgConnectionAwareCluster postgresCluster = new PgConnectionAwareCluster()) {
            final PgConnection firstConnection = postgresCluster.getFirstPgConnection();
            final PgConnection secondConnection = postgresCluster.getSecondPgConnection();

            assertThat(primaryHostDeterminer.isPrimary(firstConnection))
                .as("First connection is primary")
                .isTrue();

            assertThat(primaryHostDeterminer.isPrimary(secondConnection))
                .as("Second connection is not primary")
                .isFalse();

            postgresCluster.stopFirstContainer();

            Awaitility
                .await("Second node becomes primary")
                .atMost(PgConnectionAwareCluster.MAX_WAIT_INTERVAL_SECONDS)
                .with()
                .pollInterval(Duration.ofSeconds(2))
                .until(() -> primaryHostDeterminer.isPrimary(secondConnection));

            assertThat(primaryHostDeterminer.isPrimary(secondConnection))
                .as("Second connection is primary")
                .isTrue();
        }
    }
}
