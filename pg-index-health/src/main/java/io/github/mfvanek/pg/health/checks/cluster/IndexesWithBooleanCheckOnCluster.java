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
import io.github.mfvanek.pg.core.checks.host.IndexesWithBooleanCheckOnHost;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import javax.annotation.Nonnull;

/**
 * Check for indexes that contain boolean values on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.11.0
 */
public class IndexesWithBooleanCheckOnCluster extends AbstractCheckOnCluster<IndexWithColumns> {

    public IndexesWithBooleanCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, IndexesWithBooleanCheckOnHost::new);
    }
}
