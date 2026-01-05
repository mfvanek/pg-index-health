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
import io.github.mfvanek.pg.core.checks.host.IndexesWithTimestampInTheMiddleCheckOnHost;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

/**
 * Check for indexes in which columns with the timestamp\timestamptz type are not the last on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.2
 */
public class IndexesWithTimestampInTheMiddleCheckOnCluster extends AbstractCheckOnCluster<IndexWithColumns> {

    /**
     * Constructs a new instance of {@code IndexesWithTimestampInTheMiddleCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public IndexesWithTimestampInTheMiddleCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, IndexesWithTimestampInTheMiddleCheckOnHost::new);
    }
}
