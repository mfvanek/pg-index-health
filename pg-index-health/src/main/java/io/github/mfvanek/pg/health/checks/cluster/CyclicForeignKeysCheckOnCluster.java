/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.core.checks.host.CyclicForeignKeysCheckOnHost;
import io.github.mfvanek.pg.model.constraint.ForeignKeyCycle;

/**
 * Check for cyclic foreign keys (chains of foreign keys that lead from a table back to itself through one or more other tables) on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.50.0
 */
public class CyclicForeignKeysCheckOnCluster extends AbstractCheckOnCluster<ForeignKeyCycle> {

    /**
     * Constructs a new instance of {@code CyclicForeignKeysCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public CyclicForeignKeysCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, CyclicForeignKeysCheckOnHost::new);
    }
}
