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
import io.github.mfvanek.pg.core.checks.host.NotValidConstraintsCheckOnHost;
import io.github.mfvanek.pg.model.constraint.Constraint;

import javax.annotation.Nonnull;

/**
 * Check for not valid constraint on all hosts in the cluster.
 *
 * @author Blohny
 * @since 0.11.0
 */
public class NotValidConstraintsCheckOnCluster extends AbstractCheckOnCluster<Constraint> {

    public NotValidConstraintsCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, NotValidConstraintsCheckOnHost::new);
    }
}
