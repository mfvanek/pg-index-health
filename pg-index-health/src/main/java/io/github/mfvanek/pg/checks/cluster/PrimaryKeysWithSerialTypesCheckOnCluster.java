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
import io.github.mfvanek.pg.core.checks.host.PrimaryKeysWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

import javax.annotation.Nonnull;

/**
 * Check for primary keys columns with serial types (smallserial/serial/bigserial) on all hosts in the cluster.
 * New "generated as identity" syntax should be used instead.
 *
 * @author Vadim Khizhin
 * @since 0.13.0
 */
public class PrimaryKeysWithSerialTypesCheckOnCluster extends AbstractCheckOnCluster<ColumnWithSerialType> {

    public PrimaryKeysWithSerialTypesCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, PrimaryKeysWithSerialTypesCheckOnHost::new);
    }
}
