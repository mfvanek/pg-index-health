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
import io.github.mfvanek.pg.core.checks.host.ForeignKeysWithUnmatchedColumnTypeCheckOnHost;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

import javax.annotation.Nonnull;

/**
 * Check for foreign keys where the type of the constrained column does not match the type in the referenced table on all hosts in the cluster.
 * <p>
 * The column types in the referring and target relation must match.
 * For example, a column with the {@code integer} type should refer to a column with the {@code integer} type.
 * This eliminates unnecessary conversions at the DBMS level and in the application code,
 * and reduces the number of errors that may appear due to type inconsistencies in the future.
 *
 * @author Ivan Vahrushev
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-constraint.html">pg_constraint</a>
 * @since 0.13.2
 */
public class ForeignKeysWithUnmatchedColumnTypeCheckOnCluster extends AbstractCheckOnCluster<ForeignKey> {

    public ForeignKeysWithUnmatchedColumnTypeCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ForeignKeysWithUnmatchedColumnTypeCheckOnHost::new);
    }
}
