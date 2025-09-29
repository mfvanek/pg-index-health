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
import io.github.mfvanek.pg.core.checks.host.ForeignKeysNotCoveredWithIndexCheckOnHost;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

/**
 * Check for foreign keys without associated indexes on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class ForeignKeysNotCoveredWithIndexCheckOnCluster extends AbstractCheckOnCluster<ForeignKey> {

    /**
     * Constructs a new instance of {@code ForeignKeysNotCoveredWithIndexCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public ForeignKeysNotCoveredWithIndexCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ForeignKeysNotCoveredWithIndexCheckOnHost::new);
    }
}
