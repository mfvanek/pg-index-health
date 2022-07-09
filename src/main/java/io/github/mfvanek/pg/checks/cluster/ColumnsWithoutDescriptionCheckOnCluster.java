/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.host.ColumnsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnCluster;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.model.table.Column;

import javax.annotation.Nonnull;

/**
 * Check for columns without description on all hosts in the cluster.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public class ColumnsWithoutDescriptionCheckOnCluster extends AbstractCheckOnCluster<Column> {

    public ColumnsWithoutDescriptionCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ColumnsWithoutDescriptionCheckOnHost::new);
    }
}
