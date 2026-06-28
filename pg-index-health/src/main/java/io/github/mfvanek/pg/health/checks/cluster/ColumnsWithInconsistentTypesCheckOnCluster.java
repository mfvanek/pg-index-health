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
import io.github.mfvanek.pg.core.checks.host.ColumnsWithInconsistentTypesCheckOnHost;
import io.github.mfvanek.pg.model.column.ColumnWithType;

/**
 * Check for columns that share the same name but have different data types across tables on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.1
 */
public class ColumnsWithInconsistentTypesCheckOnCluster extends AbstractCheckOnCluster<ColumnWithType> {

    /**
     * Constructs a new instance of {@code ColumnsWithInconsistentTypesCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public ColumnsWithInconsistentTypesCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ColumnsWithInconsistentTypesCheckOnHost::new);
    }
}
