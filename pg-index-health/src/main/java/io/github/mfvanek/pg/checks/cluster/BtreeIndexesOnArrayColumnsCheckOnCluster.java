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

import io.github.mfvanek.pg.checks.host.BtreeIndexesOnArrayColumnsCheckOnHost;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import javax.annotation.Nonnull;

/**
 * Check for B-tree indexes on array columns on all hosts in the cluster.
 * GIN-index should be used instead for such columns.
 *
 * @author Vadim Khizhin
 * @since 0.10.4
 */
public class BtreeIndexesOnArrayColumnsCheckOnCluster extends AbstractCheckOnCluster<IndexWithColumns> {

    public BtreeIndexesOnArrayColumnsCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, BtreeIndexesOnArrayColumnsCheckOnHost::new);
    }
}
