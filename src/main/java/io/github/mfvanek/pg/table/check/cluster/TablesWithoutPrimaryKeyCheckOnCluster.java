/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.table.check.cluster;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnCluster;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.table.check.host.TablesWithoutPrimaryKeyCheckOnHost;

import javax.annotation.Nonnull;

/**
 * Check for tables without primary key on all hosts in the cluster.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public class TablesWithoutPrimaryKeyCheckOnCluster extends AbstractCheckOnCluster<Table> {

    public TablesWithoutPrimaryKeyCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, TablesWithoutPrimaryKeyCheckOnHost::new);
    }
}
