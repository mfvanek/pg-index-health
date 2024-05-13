/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A standard implementation of {@code PgHost} interface.
 *
 * @author Ivan Vakhrushev
 * @see PgHost
 */
@Immutable
public class PgHostImpl implements PgHost {

    private final String pgUrl;
    private final String hostName;
    private final int port;
    private final boolean maybePrimary;

    private PgHostImpl(@Nonnull final String pgUrl,
                       @Nonnull final String hostName,
                       final int port,
                       final boolean maybePrimary) {
        this.pgUrl = PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        this.hostName = PgConnectionValidators.hostNameNotBlank(hostName);
        this.port = PgConnectionValidators.portInAcceptableRange(port);
        this.maybePrimary = maybePrimary;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getPgUrl() {
        return pgUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getName() {
        return hostName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBePrimary() {
        return maybePrimary;
    }

    /**
     * Constructs a {@code PgHost} object from given JDBC connection string.
     *
     * @param pgUrl connection string to a database in JDBC format
     * @return {@code PgHost}
     */
    @Nonnull
    public static PgHost ofUrl(@Nonnull final String pgUrl) {
        final List<Map.Entry<String, Integer>> extractHostNames = PgUrlParser.extractHostNames(pgUrl);
        if (extractHostNames.size() > 1) {
            throw new IllegalArgumentException("pgUrl couldn't contain multiple hosts");
        }

        final Map.Entry<String, Integer> host = extractHostNames.get(0);
        return new PgHostImpl(pgUrl, host.getKey(), host.getValue(), !PgUrlParser.isReplicaUrl(pgUrl));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        // Ignoring Liskov substitution principle
        if (!(other instanceof PgHostImpl)) {
            return false;
        }

        final PgHostImpl pgHost = (PgHostImpl) other;
        return port == pgHost.port && Objects.equals(hostName, pgHost.hostName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(hostName, port);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return PgHostImpl.class.getSimpleName() + '{' +
                "pgUrl='" + pgUrl + '\'' +
                ", hostName=" + hostName +
                ", port=" + port +
                ", maybePrimary=" + maybePrimary +
                '}';
    }
}
