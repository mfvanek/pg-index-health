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
import io.github.mfvanek.pg.core.checks.host.SequenceOverflowCheckOnHost;
import io.github.mfvanek.pg.model.sequence.SequenceState;

/**
 * Check for sequence overflow on all hosts in the cluster.
 *
 * @author Blohny
 * @since 0.12.0
 */
public class SequenceOverflowCheckOnCluster extends AbstractCheckOnCluster<SequenceState> {

    /**
     * Constructs a new instance of {@code SequenceOverflowCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public SequenceOverflowCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, SequenceOverflowCheckOnHost::new);
    }
}
