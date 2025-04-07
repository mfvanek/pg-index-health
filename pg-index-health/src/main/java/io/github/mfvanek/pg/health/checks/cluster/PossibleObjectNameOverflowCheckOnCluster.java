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
import io.github.mfvanek.pg.core.checks.host.PossibleObjectNameOverflowCheckOnHost;
import io.github.mfvanek.pg.model.dbobject.AnyObject;

import javax.annotation.Nonnull;

/**
 * Check for objects whose names have a length of {@code max_identifier_length} (usually it is 63) on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/limits.html">PostgreSQL Limits</a>
 * @since 0.13.2
 */
public class PossibleObjectNameOverflowCheckOnCluster extends AbstractCheckOnCluster<AnyObject> {

    public PossibleObjectNameOverflowCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, PossibleObjectNameOverflowCheckOnHost::new);
    }
}
