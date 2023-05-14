/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import io.github.mfvanek.pg.testing.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class PostgresVersionHolder implements PostgresVersionAware {

    private final String pgVersion;

    PostgresVersionHolder(final String pgVersion) {
        this.pgVersion = Objects.requireNonNull(pgVersion, "pgVersion cannot be null");
    }

    private int getMajorVersion() {
        final String[] parts = pgVersion.split("\\.");
        return Integer.parseInt(parts[0]);
    }

    @Nonnull
    public String getVersion() {
        return pgVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCumulativeStatisticsSystemSupported() {
        return getMajorVersion() >= 15;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProceduresSupported() {
        return getMajorVersion() >= 11;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOutParametersInProcedureSupported() {
        return isProceduresSupported() && getMajorVersion() >= 14;
    }

    @ExcludeFromJacocoGeneratedReport
    @Nonnull
    private static String preparePostgresVersion() {
        final String pgVersion = System.getenv("TEST_PG_VERSION");
        if (pgVersion != null) {
            return pgVersion;
        }
        return "15.2";
    }

    public static PostgresVersionHolder forCluster(@Nullable final String forcedPostgresVersion) {
        final String pgVersion = forcedPostgresVersion != null ?
                forcedPostgresVersion : preparePostgresVersion();
        // Bitnami images use semantic versioning with three digits
        return new PostgresVersionHolder(pgVersion + ".0");
    }

    public static PostgresVersionHolder forSingleNode() {
        return new PostgresVersionHolder(preparePostgresVersion());
    }
}
