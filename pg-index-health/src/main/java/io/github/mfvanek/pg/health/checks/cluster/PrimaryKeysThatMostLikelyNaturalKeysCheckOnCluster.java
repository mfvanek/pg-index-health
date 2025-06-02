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
import io.github.mfvanek.pg.core.checks.host.PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

/**
 * Check for primary keys that are most likely natural keys on all hosts in the cluster.
 * <p>
 * It is better to use surrogate keys instead of natural ones.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://blog.ploeh.dk/2024/06/03/youll-regret-using-natural-keys/">You'll regret using natural keys</a>
 * @since 0.15.0
 */
public class PrimaryKeysThatMostLikelyNaturalKeysCheckOnCluster extends AbstractCheckOnCluster<IndexWithColumns> {

    public PrimaryKeysThatMostLikelyNaturalKeysCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost::new);
    }
}
