/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithFixedLengthVarcharCheckOnHost;
import io.github.mfvanek.pg.model.column.Column;

import javax.annotation.Nonnull;

/**
 * Check for columns with fixed length varchar type on all hosts in the cluster.
 *
 * @author Diana Gilfanova
 * @since 0.14.6
 */
public class ColumnsWithFixedLengthVarcharCheckOnCluster extends AbstractCheckOnCluster<Column> {

    public ColumnsWithFixedLengthVarcharCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ColumnsWithFixedLengthVarcharCheckOnHost::new);
    }
}
