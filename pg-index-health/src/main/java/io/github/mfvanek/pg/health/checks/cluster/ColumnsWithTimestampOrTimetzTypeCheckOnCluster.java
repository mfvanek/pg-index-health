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
import io.github.mfvanek.pg.core.checks.host.ColumnsWithTimestampOrTimetzTypeCheckOnHost;
import io.github.mfvanek.pg.model.column.ColumnWithType;

/**
 * Check for columns with {@code timestamp} or {@code timetz} type on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnsWithTimestampOrTimetzTypeCheckOnCluster extends AbstractCheckOnCluster<ColumnWithType> {

    /**
     * Constructs a new instance of {@code ColumnsWithTimestampOrTimetzTypeCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public ColumnsWithTimestampOrTimetzTypeCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ColumnsWithTimestampOrTimetzTypeCheckOnHost::new);
    }
}
