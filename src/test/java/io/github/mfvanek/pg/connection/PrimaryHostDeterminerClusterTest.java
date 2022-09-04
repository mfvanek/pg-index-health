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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Primary host determiner tests on cluster.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
class PrimaryHostDeterminerClusterTest extends ClusterAwareTestBase {

    private final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();

    @Test
    void correctPrimaryDetection() {
        final PgConnection firstConnection = getFirstPgConnection();
        final PgConnection secondConnection = getSecondPgConnection();

        assertThat(primaryHostDeterminer.isPrimary(firstConnection))
                .as("First connection is primary")
                .isTrue();

        assertThat(primaryHostDeterminer.isPrimary(secondConnection))
                .as("Second connection is not primary")
                .isFalse();

        stopFirstContainer();

        Awaitility
                .await("Second node becomes primary")
                .atMost(Duration.ofSeconds(120)) // on some systems promoting to primary could take up to minute and even more
                .with()
                .pollInterval(Duration.ofSeconds(2))
                .until(() -> primaryHostDeterminer.isPrimary(secondConnection));

        assertThat(primaryHostDeterminer.isPrimary(secondConnection))
                .as("Second connection is primary")
                .isTrue();
    }
}
