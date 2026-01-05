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
import io.github.mfvanek.pg.core.checks.host.DuplicatedForeignKeysCheckOnHost;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;

/**
 * Check for duplicated (completely identical) foreign keys on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.1
 */
public class DuplicatedForeignKeysCheckOnCluster extends AbstractCheckOnCluster<DuplicatedForeignKeys> {

    /**
     * Constructs a new instance of {@code DuplicatedForeignKeysCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public DuplicatedForeignKeysCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, DuplicatedForeignKeysCheckOnHost::new);
    }
}
