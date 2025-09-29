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
import io.github.mfvanek.pg.core.checks.host.ColumnsNotFollowingNamingConventionCheckOnHost;
import io.github.mfvanek.pg.model.column.Column;

/**
 * Check for columns whose names do not follow the naming convention (that have to be enclosed in double-quotes) on all hosts in the cluster.
 * <p>
 * You should avoid using quoted column names.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/17/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS">PostgreSQL Naming Convention</a>
 * @since 0.14.6
 */
public class ColumnsNotFollowingNamingConventionCheckOnCluster extends AbstractCheckOnCluster<Column> {

    /**
     * Constructs a new instance of {@code ColumnsNotFollowingNamingConventionCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public ColumnsNotFollowingNamingConventionCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ColumnsNotFollowingNamingConventionCheckOnHost::new);
    }
}
