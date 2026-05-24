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
import io.github.mfvanek.pg.core.checks.host.SelfReferencedForeignKeysCheckOnHost;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

/**
 * Check for self-referenced foreign keys without {@code ON DELETE CASCADE} or {@code ON DELETE SET NULL}
 * on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.1
 */
public class SelfReferencedForeignKeysCheckOnCluster extends AbstractCheckOnCluster<ForeignKey> {

    /**
     * Constructs a new instance of {@code SelfReferencedForeignKeysCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public SelfReferencedForeignKeysCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, SelfReferencedForeignKeysCheckOnHost::new);
    }
}
