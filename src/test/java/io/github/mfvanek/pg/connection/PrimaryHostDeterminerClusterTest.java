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
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Primary host determiner tests on cluster.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
class PrimaryHostDeterminerClusterTest {
    private final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();
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
    void correctPrimaryDetection() {
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
                .atMost(Duration.ofSeconds(120)) // on some systems promoting to primary could take up to minute and even more
                .with()
                .pollInterval(Duration.ofSeconds(2))
                .until(() -> primaryHostDeterminer.isPrimary(secondConnection));

        assertThat(primaryHostDeterminer.isPrimary(secondConnection))
                .as("Second connection is primary")
                .isTrue();
    }
}
