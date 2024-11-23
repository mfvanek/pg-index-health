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
import io.github.mfvanek.pg.core.checks.host.FunctionsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.model.function.StoredFunction;

import javax.annotation.Nonnull;

/**
 * Check for procedures/functions without description on all hosts in the cluster.
 *
 * @author Ivan Vahrushev
 * @since 0.7.0
 */
public class FunctionsWithoutDescriptionCheckOnCluster extends AbstractCheckOnCluster<StoredFunction> {

    public FunctionsWithoutDescriptionCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, FunctionsWithoutDescriptionCheckOnHost::new);
    }
}
