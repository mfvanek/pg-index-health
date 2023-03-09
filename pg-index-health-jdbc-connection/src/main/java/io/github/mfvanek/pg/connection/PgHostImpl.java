/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
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

@Immutable
public class PgHostImpl implements PgHost {

    private final String pgUrl;
    private final String hostName;
    private final int port;
    private final boolean maybePrimary;

    private PgHostImpl(@Nonnull final String pgUrl,
                       @Nonnull final String hostName,
                       final int port,
                       @SuppressWarnings("unused") final boolean withValidation, //NOSONAR
                       final boolean maybePrimary) {
        this.pgUrl = PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        this.hostName = Objects.requireNonNull(hostName, "hostName cannot be null");
        this.port = port;
        this.maybePrimary = maybePrimary;
    }

    private PgHostImpl(@Nonnull final String hostName, @Nonnull final Integer port, final boolean maybePrimary) {
        Objects.requireNonNull(hostName, "hostName cannot be null");
        this.hostName = hostName;
        this.port = port;
        this.pgUrl = PgUrlParser.URL_HEADER + hostName;
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

    @Nonnull
    public static PgHost ofPrimary() {
        return new PgHostImpl("primary", 5432, true);
    }

    @Nonnull
    public static PgHost ofPrimary(@Nonnull final Integer port) {
        return new PgHostImpl("primary", port, true);
    }

    @Nonnull
    public static PgHost ofUrl(@Nonnull final String pgUrl) {
        final List<Map.Entry<String, Integer>> extractHostNames = PgUrlParser.extractHostNames(pgUrl);
        if (extractHostNames.size() > 1) {
            throw new IllegalArgumentException("pgUrl couldn't contain multiple hosts");
        }

        final Map.Entry<String, Integer> host = extractHostNames.get(0);
        return new PgHostImpl(pgUrl, host.getKey(), host.getValue(), true, !PgUrlParser.isReplicaUrl(pgUrl));
    }

    @Nonnull
    public static PgHost ofName(@Nonnull final String hostName) {
        return new PgHostImpl(hostName, 5432, true);
    }

    @Nonnull
    public static PgHost ofName(@Nonnull final String hostName, final int port) {
        if (port < 1 || port > 65_535) {
            throw new IllegalArgumentException("the port number must be in the range from 1 to 65535");
        }
        return new PgHostImpl(hostName, port, true);
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
        return Objects.equals(hostName, pgHost.hostName) && port == pgHost.port;
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
