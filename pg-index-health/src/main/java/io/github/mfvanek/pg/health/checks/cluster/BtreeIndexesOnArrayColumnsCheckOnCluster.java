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
import io.github.mfvanek.pg.core.checks.host.BtreeIndexesOnArrayColumnsCheckOnHost;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

/**
 * Check for B-tree indexes on array columns on all hosts in the cluster.
 * GIN-index should be used instead for such columns.
 *
 * @author Vadim Khizhin
 * @since 0.11.0
 */
public class BtreeIndexesOnArrayColumnsCheckOnCluster extends AbstractCheckOnCluster<IndexWithColumns> {

    /**
     * Constructs a new instance of {@code BtreeIndexesOnArrayColumnsCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public BtreeIndexesOnArrayColumnsCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, BtreeIndexesOnArrayColumnsCheckOnHost::new);
    }
}
