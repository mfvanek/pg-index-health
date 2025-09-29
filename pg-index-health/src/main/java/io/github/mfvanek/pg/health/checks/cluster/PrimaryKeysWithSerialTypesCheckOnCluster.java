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
import io.github.mfvanek.pg.core.checks.host.PrimaryKeysWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

/**
 * Check for primary keys columns with serial types (smallserial/serial/bigserial) on all hosts in the cluster.
 * New "generated as identity" syntax should be used instead.
 *
 * @author Vadim Khizhin
 * @since 0.13.0
 */
public class PrimaryKeysWithSerialTypesCheckOnCluster extends AbstractCheckOnCluster<ColumnWithSerialType> {

    /**
     * Constructs a new instance of {@code PrimaryKeysWithSerialTypesCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public PrimaryKeysWithSerialTypesCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, PrimaryKeysWithSerialTypesCheckOnHost::new);
    }
}
