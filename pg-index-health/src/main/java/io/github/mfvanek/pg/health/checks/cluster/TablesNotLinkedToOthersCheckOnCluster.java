/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.core.checks.host.TablesNotLinkedToOthersCheckOnHost;
import io.github.mfvanek.pg.model.table.Table;

import javax.annotation.Nonnull;

/**
 * Check for tables that are not linked to other tables on all hosts in the cluster.
 * <p>
 * These are often service tables that are not part of the project, or
 * tables that are no longer in use or were created by mistake, but were not deleted in a timely manner.
 *
 * @author Ivan Vahrushev
 * @since 0.13.2
 */
public class TablesNotLinkedToOthersCheckOnCluster extends AbstractCheckOnCluster<Table> {

    public TablesNotLinkedToOthersCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, TablesNotLinkedToOthersCheckOnHost::new);
    }
}
