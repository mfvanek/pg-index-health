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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PgHostImpl implements PgHost {

    private final String pgUrl;
    private final SortedSet<String> hostNames;
    private final boolean maybePrimary;

    private PgHostImpl(@Nonnull final String pgUrl,
                       @SuppressWarnings("unused") final boolean withValidation, //NOSONAR
                       final boolean maybePrimary) {
        this.pgUrl = PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        this.hostNames = PgUrlParser.extractHostNames(pgUrl);
        this.maybePrimary = maybePrimary;
    }

    private PgHostImpl(@Nonnull final String hostName, final boolean maybePrimary) {
        Objects.requireNonNull(hostName, "hostName cannot be null");
        this.hostNames = Collections.unmodifiableSortedSet(new TreeSet<>(List.of(hostName)));
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
        return hostNames.size() == 1 ?
                hostNames.iterator().next() :
                "One of " + hostNames;
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
        return new PgHostImpl("primary", true);
    }

    @Nonnull
    public static PgHost ofUrl(@Nonnull final String pgUrl) {
        return new PgHostImpl(pgUrl, true, !PgUrlParser.isReplicaUrl(pgUrl));
    }

    @Nonnull
    public static PgHost ofName(@Nonnull final String hostName) {
        return new PgHostImpl(hostName, true);
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
        return Objects.equals(hostNames, pgHost.hostNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(hostNames);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return PgHostImpl.class.getSimpleName() + '{' +
                "pgUrl='" + pgUrl + '\'' +
                ", hostNames=" + hostNames +
                ", maybePrimary=" + maybePrimary +
                '}';
    }
}
