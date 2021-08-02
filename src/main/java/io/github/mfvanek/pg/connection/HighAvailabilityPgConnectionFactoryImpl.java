/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public class HighAvailabilityPgConnectionFactoryImpl implements HighAvailabilityPgConnectionFactory {

    private final PgConnectionFactory pgConnectionFactory;
    private final PrimaryHostDeterminer primaryHostDeterminer;

    public HighAvailabilityPgConnectionFactoryImpl(@Nonnull final PgConnectionFactory pgConnectionFactory,
                                                   @Nonnull final PrimaryHostDeterminer primaryHostDeterminer) {
        this.pgConnectionFactory = Objects.requireNonNull(pgConnectionFactory);
        this.primaryHostDeterminer = Objects.requireNonNull(primaryHostDeterminer);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public HighAvailabilityPgConnection of(@Nonnull final ConnectionCredentials credentials) {
        final Map<String, PgConnection> connectionsToAllHostsInCluster = new LinkedHashMap<>();
        credentials.getConnectionUrls().forEach(
                url -> addDataSourcesForAllHostsFromUrl(connectionsToAllHostsInCluster, url, credentials));
        final PgConnection connectionToPrimary = findConnectionToPrimary(connectionsToAllHostsInCluster);
        final Set<PgConnection> pgConnections = new HashSet<>(connectionsToAllHostsInCluster.values());
        return HighAvailabilityPgConnectionImpl.of(connectionToPrimary, pgConnections);
    }

    private void addDataSourcesForAllHostsFromUrl(@Nonnull final Map<String, PgConnection> connectionsToAllHostsInCluster,
                                                  @Nonnull final String anyUrl,
                                                  @Nonnull final ConnectionCredentials credentials) {
        final List<Pair<String, String>> allHosts = PgUrlParser.extractNameWithPortAndUrlForEachHost(anyUrl);
        for (Pair<String, String> host : allHosts) {
            connectionsToAllHostsInCluster.computeIfAbsent(host.getKey(),
                    h -> pgConnectionFactory.forUrl(host.getValue(), credentials.getUserName(), credentials.getPassword()));
        }
    }

    private PgConnection findConnectionToPrimary(final Map<String, PgConnection> connectionsToAllHostsInCluster) {
        for (PgConnection pgConnection : connectionsToAllHostsInCluster.values()) {
            if (primaryHostDeterminer.isPrimary(pgConnection)) {
                return pgConnection;
            }
        }
        throw new NoSuchElementException("Connection to primary host not found in " + connectionsToAllHostsInCluster);
    }
}
