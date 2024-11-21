/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

import javax.annotation.Nonnull;

/**
 * Check for columns of serial types that are not primary keys on all hosts in the cluster.
 *
 * @author Ivan Vahrushev
 * @since 0.6.2
 */
public class ColumnsWithSerialTypesCheckOnCluster extends AbstractCheckOnCluster<ColumnWithSerialType> {

    public ColumnsWithSerialTypesCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ColumnsWithSerialTypesCheckOnHost::new);
    }
}
