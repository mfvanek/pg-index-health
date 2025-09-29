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
import io.github.mfvanek.pg.core.checks.host.ColumnsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.model.column.Column;

/**
 * Check for columns without description on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class ColumnsWithoutDescriptionCheckOnCluster extends AbstractCheckOnCluster<Column> {

    /**
     * Constructs a new instance of {@code ColumnsWithoutDescriptionCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public ColumnsWithoutDescriptionCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ColumnsWithoutDescriptionCheckOnHost::new);
    }
}
