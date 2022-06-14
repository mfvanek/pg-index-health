/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.check.cluster;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnCluster;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.index.check.host.IndexesWithBloatCheckOnHost;
import io.github.mfvanek.pg.model.index.IndexWithBloat;

import javax.annotation.Nonnull;

/**
 * Check for indexes bloat on all hosts in the cluster.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public class IndexesWithBloatCheckOnCluster extends AbstractCheckOnCluster<IndexWithBloat> {

    public IndexesWithBloatCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, IndexesWithBloatCheckOnHost::new);
    }
}
