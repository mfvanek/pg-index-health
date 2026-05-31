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
import io.github.mfvanek.pg.core.checks.host.TablesWithIncrementingColumnsCheckOnHost;
import io.github.mfvanek.pg.model.table.TableWithColumns;

/**
 * Check for tables with incrementing column names (e.g., {@code phone1}, {@code phone2}) on all hosts in the cluster.
 * Such columns indicate de-normalization that could be replaced with a separate child table and a foreign key.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.1
 */
public class TablesWithIncrementingColumnsCheckOnCluster extends AbstractCheckOnCluster<TableWithColumns> {

    /**
     * Constructs a new instance of {@code TablesWithIncrementingColumnsCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public TablesWithIncrementingColumnsCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, TablesWithIncrementingColumnsCheckOnHost::new);
    }
}
