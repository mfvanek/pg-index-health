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
import io.github.mfvanek.pg.core.checks.host.ColumnsWithCharTypeCheckOnHost;
import io.github.mfvanek.pg.model.column.ColumnWithType;

/**
 * Check for columns of type {@code char}, {@code char(n)}, {@code character(n)} or {@code bpchar(n)} on all hosts in the cluster.
 * <p>
 * Do not use the type {@code char(n)}. It is better to use the type {@code text} instead.
 * Any string you insert into a {@code char(n)} field will be padded with spaces to the declared width.
 * The space-padding does waste space but does not make operations on it any faster.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/datatype-character.html">Character Types </a>
 * @since 0.30.1
 */
public class ColumnsWithCharTypeCheckOnCluster extends AbstractCheckOnCluster<ColumnWithType> {

    /**
     * Constructs a new instance of {@code ColumnsWithCharTypeCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public ColumnsWithCharTypeCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ColumnsWithCharTypeCheckOnHost::new);
    }
}
