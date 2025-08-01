/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PrimaryHostDeterminer;
import io.github.mfvanek.pg.connection.host.PgUrlParser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class HighAvailabilityPgConnectionFactoryImpl implements HighAvailabilityPgConnectionFactory {

    private final PgConnectionFactory pgConnectionFactory;
    private final PrimaryHostDeterminer primaryHostDeterminer;

    public HighAvailabilityPgConnectionFactoryImpl(final PgConnectionFactory pgConnectionFactory,
                                                   final PrimaryHostDeterminer primaryHostDeterminer) {
        this.pgConnectionFactory = Objects.requireNonNull(pgConnectionFactory);
        this.primaryHostDeterminer = Objects.requireNonNull(primaryHostDeterminer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HighAvailabilityPgConnection of(final ConnectionCredentials credentials) {
        Objects.requireNonNull(credentials, "credentials cannot be null");
        final Map<String, PgConnection> connectionsToAllHostsInCluster = new LinkedHashMap<>();
        credentials.getConnectionUrls().forEach(
            url -> addDataSourcesForAllHostsFromUrl(connectionsToAllHostsInCluster, url, credentials));
        final PgConnection connectionToPrimary = findConnectionToPrimary(connectionsToAllHostsInCluster);
        return HighAvailabilityPgConnectionImpl.of(connectionToPrimary, connectionsToAllHostsInCluster.values());
    }

    private void addDataSourcesForAllHostsFromUrl(final Map<String, PgConnection> connectionsToAllHostsInCluster,
                                                  final String anyUrl,
                                                  final ConnectionCredentials credentials) {
        final List<Map.Entry<String, String>> allHosts = PgUrlParser.extractNameWithPortAndUrlForEachHost(anyUrl);
        for (final Map.Entry<String, String> host : allHosts) {
            connectionsToAllHostsInCluster.computeIfAbsent(host.getKey(),
                h -> pgConnectionFactory.forUrl(host.getValue(), credentials.getUserName(), credentials.getPassword()));
        }
    }

    private PgConnection findConnectionToPrimary(final Map<String, PgConnection> connectionsToAllHostsInCluster) {
        for (final PgConnection pgConnection : connectionsToAllHostsInCluster.values()) {
            if (primaryHostDeterminer.isPrimary(pgConnection)) {
                return pgConnection;
            }
        }
        throw new NoSuchElementException("Connection to primary host not found in " + connectionsToAllHostsInCluster);
    }
}
