/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import io.github.mfvanek.pg.model.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Locale;
import java.util.Objects;

/**
 * A helper class to obtain a PostgreSQL version to run with Testcontainers.
 *
 * @author Ivan Vakhrushev
 */
public final class PostgresVersionHolder implements PostgresVersionAware {

    private final String pgVersion;

    PostgresVersionHolder(final String pgVersion) {
        this.pgVersion = Objects.requireNonNull(pgVersion, "pgVersion cannot be null");
    }

    private int getMajorVersion() {
        final String[] parts = pgVersion.split("\\.");
        return Integer.parseInt(parts[0]);
    }

    /**
     * Retrieves the target PostgreSQL version to run with Testcontainers.
     *
     * @return PostgreSQL version to run
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMountVolume() {
        final int majorVersion = getMajorVersion();
        if (majorVersion >= 18) {
            return String.format(Locale.ROOT, "/var/lib/postgresql/%d/docker", majorVersion);
        }
        return "/var/lib/postgresql/data";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotNullConstraintsSupported() {
        return getMajorVersion() >= 18;
    }

    @ExcludeFromJacocoGeneratedReport
    private static String preparePostgresVersion() {
        final String pgVersion = System.getenv("TEST_PG_VERSION");
        if (pgVersion != null) {
            return pgVersion;
        }
        return "18.0";
    }

    /**
     * Creates {@code PostgresVersionHolder} for single node installation.
     * The version is taken from the environment variable {@code TEST_PG_VERSION} if it is set,
     * otherwise the default version {@code 18.0} is used.
     *
     * @return {@code PostgresVersionHolder}
     */
    public static PostgresVersionHolder forSingleNode() {
        return new PostgresVersionHolder(preparePostgresVersion());
    }

    /**
     * Creates {@code PostgresVersionHolder} with the given version for single node installation.
     *
     * @param pgVersion given PostgreSQL version
     * @return {@code PostgresVersionHolder}
     * @since 0.9.2
     */
    public static PostgresVersionHolder forSingleNode(final String pgVersion) {
        return new PostgresVersionHolder(pgVersion);
    }
}
