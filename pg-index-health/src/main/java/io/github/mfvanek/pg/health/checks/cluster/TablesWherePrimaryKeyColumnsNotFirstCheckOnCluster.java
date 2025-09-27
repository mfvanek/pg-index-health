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
import io.github.mfvanek.pg.core.checks.host.TablesWherePrimaryKeyColumnsNotFirstCheckOnHost;
import io.github.mfvanek.pg.model.table.Table;

/**
 * Check for tables where the primary key column is not the first column in the table on all hosts in the cluster.
 * <p>
 * Putting the primary key as the first column improves readability, consistency, and expectations, especially in teams or large schemas.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TablesWherePrimaryKeyColumnsNotFirstCheckOnCluster extends AbstractCheckOnCluster<Table> {

    public TablesWherePrimaryKeyColumnsNotFirstCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, TablesWherePrimaryKeyColumnsNotFirstCheckOnHost::new);
    }
}
