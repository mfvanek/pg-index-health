/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.core.checks.host.FunctionsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.model.function.StoredFunction;

/**
 * Check for procedures/functions without description on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.7.0
 */
public class FunctionsWithoutDescriptionCheckOnCluster extends AbstractCheckOnCluster<StoredFunction> {

    public FunctionsWithoutDescriptionCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, FunctionsWithoutDescriptionCheckOnHost::new);
    }
}
