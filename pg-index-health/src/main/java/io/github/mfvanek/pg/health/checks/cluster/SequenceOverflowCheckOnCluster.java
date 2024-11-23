/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.core.checks.host.SequenceOverflowCheckOnHost;
import io.github.mfvanek.pg.model.sequence.SequenceState;

import javax.annotation.Nonnull;

/**
 * Check for sequence overflow on all hosts in the cluster.
 *
 * @author Blohny
 * @since 0.12.0
 */
public class SequenceOverflowCheckOnCluster extends AbstractCheckOnCluster<SequenceState> {

    public SequenceOverflowCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, SequenceOverflowCheckOnHost::new);
    }
}
