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
import io.github.mfvanek.pg.core.checks.host.IndexesWithUnnecessaryWhereClauseCheckOnHost;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

/**
 * Check for indexes with unnecessary where-clause on not null column on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.14.6
 */
public class IndexesWithUnnecessaryWhereClauseCheckOnCluster extends AbstractCheckOnCluster<IndexWithColumns> {

    /**
     * Constructs a new instance of {@code IndexesWithUnnecessaryWhereClauseCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public IndexesWithUnnecessaryWhereClauseCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, IndexesWithUnnecessaryWhereClauseCheckOnHost::new);
    }
}
