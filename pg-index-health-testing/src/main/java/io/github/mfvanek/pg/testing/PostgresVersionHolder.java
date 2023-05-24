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
import javax.annotation.concurrent.Immutable;

/**
 * A helper class to obtain PostgreSQL version to run with Testcontainers.
 *
 * @author Ivan Vakhrushev
 */
@Immutable
final class PostgresVersionHolder implements PostgresVersionAware {

    private final String pgVersion;

    PostgresVersionHolder(@Nonnull final String pgVersion) {
        this.pgVersion = Objects.requireNonNull(pgVersion, "pgVersion cannot be null");
    }

    private int getMajorVersion() {
        final String[] parts = pgVersion.split("\\.");
        return Integer.parseInt(parts[0]);
    }

    /**
     * Gets target PostgreSQL version to run with Testcontainers.
     *
     * @return PostgreSQL version to run
     */
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
        return "15.3";
    }

    @Nonnull
    private static String toBitnamiVersion(@Nonnull final String pgVersion) {
        // Bitnami images use semantic versioning with three digits
        return pgVersion + ".0";
    }

    /**
     * Creates {@code PostgresVersionHolder} for Bitnami cluster installation.
     * The version is taken from the environment variable {@code TEST_PG_VERSION} if it is set,
     * otherwise the default version {@code 15.3.0} is used.
     *
     * @return {@code PostgresVersionHolder}
     */
    @Nonnull
    static PostgresVersionHolder forCluster() {
        return new PostgresVersionHolder(toBitnamiVersion(preparePostgresVersion()));
    }

    /**
     * Creates {@code PostgresVersionHolder} with given version for Bitnami cluster installation.
     *
     * @param pgVersion given PostgreSQL version
     * @return {@code PostgresVersionHolder}
     * @since 0.9.2
     */
    @Nonnull
    static PostgresVersionHolder forCluster(@Nonnull final String pgVersion) {
        return new PostgresVersionHolder(toBitnamiVersion(pgVersion));
    }

    /**
     * Creates {@code PostgresVersionHolder} for single node installation.
     * The version is taken from the environment variable {@code TEST_PG_VERSION} if it is set,
     * otherwise the default version {@code 15.3} is used.
     *
     * @return {@code PostgresVersionHolder}
     */
    @Nonnull
    static PostgresVersionHolder forSingleNode() {
        return new PostgresVersionHolder(preparePostgresVersion());
    }

    /**
     * Creates {@code PostgresVersionHolder} with given version for single node installation.
     *
     * @param pgVersion given PostgreSQL version
     * @return {@code PostgresVersionHolder}
     * @since 0.9.2
     */
    @Nonnull
    static PostgresVersionHolder forSingleNode(@Nonnull final String pgVersion) {
        return new PostgresVersionHolder(pgVersion);
    }
}
