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
import io.github.mfvanek.pg.core.checks.host.TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnHost;
import io.github.mfvanek.pg.model.table.Table;

/**
 * Check for tables that have all columns besides the primary key that are nullable on all hosts in the cluster.
 * <p>
 * Such tables may contain no useful data and could indicate a schema design smell.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnCluster extends AbstractCheckOnCluster<Table> {

    /**
     * Constructs a new instance of {@code TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnHost::new);
    }
}
