/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.host;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable standard implementation of {@code PgHost} interface.
 *
 * @author Ivan Vakhrushev
 * @see PgHost
 */
public final class PgHostImpl implements PgHost {

    private final String pgUrl;
    private final String hostName;
    private final int port;
    private final boolean maybePrimary;

    private PgHostImpl(final String pgUrl,
                       final String hostName,
                       final int port,
                       final boolean maybePrimary) {
        this.pgUrl = PgUrlValidators.pgUrlNotBlankAndValid(pgUrl);
        this.hostName = PgUrlValidators.hostNameNotBlank(hostName);
        this.port = PgUrlValidators.portInAcceptableRange(port);
        this.maybePrimary = maybePrimary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPgUrl() {
        return pgUrl;
    }

    /**
     * {@inheritDoc}
     */
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        // Ignoring Liskov substitution principle
        if (!(other instanceof final PgHostImpl that)) {
            return false;
        }

        return port == that.port && Objects.equals(hostName, that.hostName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(hostName, port);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return PgHostImpl.class.getSimpleName() + '{' +
            "pgUrl='" + pgUrl + '\'' +
            ", hostName=" + hostName +
            ", port=" + port +
            ", maybePrimary=" + maybePrimary +
            '}';
    }

    /**
     * Constructs a {@code PgHost} object from given JDBC connection string.
     *
     * @param pgUrl connection string to a database in JDBC format
     * @return {@code PgHost}
     */
    public static PgHost ofUrl(final String pgUrl) {
        final List<Map.Entry<String, Integer>> extractHostNames = PgUrlParser.extractHostNames(pgUrl);
        if (extractHostNames.size() > 1) {
            throw new IllegalArgumentException("pgUrl couldn't contain multiple hosts");
        }

        final Map.Entry<String, Integer> host = extractHostNames.get(0);
        return new PgHostImpl(pgUrl, host.getKey(), host.getValue(), !PgUrlParser.isReplicaUrl(pgUrl));
    }
}
