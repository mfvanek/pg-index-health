/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class HighAvailabilityPgConnectionFactoryImpl implements HighAvailabilityPgConnectionFactory {

    private final PgConnectionFactory pgConnectionFactory;
    private final PrimaryHostDeterminer primaryHostDeterminer;

    public HighAvailabilityPgConnectionFactoryImpl(@Nonnull final PgConnectionFactory pgConnectionFactory,
                                                   @Nonnull final PrimaryHostDeterminer primaryHostDeterminer) {
        this.pgConnectionFactory = Objects.requireNonNull(pgConnectionFactory);
        this.primaryHostDeterminer = Objects.requireNonNull(primaryHostDeterminer);
    }

    @Override
    @Nonnull
    public HighAvailabilityPgConnection of(@Nonnull final String writeUrl,
                                           @Nonnull final String userName,
                                           @Nonnull final String password) {
        return create(writeUrl, userName, password, null, null);
    }

    @Override
    @Nonnull
    public HighAvailabilityPgConnection of(@Nonnull final String writeUrl,
                                           @Nonnull final String userName,
                                           @Nonnull final String password,
                                           @Nonnull final String readUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(readUrl, "readUrl");
        return create(writeUrl, userName, password, readUrl, null);
    }

    @Override
    @Nonnull
    public HighAvailabilityPgConnection of(@Nonnull final String writeUrl,
                                           @Nonnull final String userName,
                                           @Nonnull final String password,
                                           @Nonnull final String readUrl,
                                           @Nonnull final String cascadeAsyncReadUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(readUrl, "readUrl");
        PgConnectionValidators.pgUrlNotBlankAndValid(cascadeAsyncReadUrl, "cascadeAsyncReadUrl");
        return create(writeUrl, userName, password, readUrl, cascadeAsyncReadUrl);
    }

    @Nonnull
    private HighAvailabilityPgConnection create(@Nonnull final String writeUrl,
                                                @Nonnull final String userName,
                                                @Nonnull final String password,
                                                @Nullable final String readUrl,
                                                @Nullable final String cascadeAsyncReadUrl) {
        final Map<String, PgConnection> connectionsToAllHostsInCluster = new LinkedHashMap<>();
        addDataSourcesForAllHostsFromUrl(connectionsToAllHostsInCluster, writeUrl, userName, password);
        if (StringUtils.isNotBlank(readUrl)) {
            addDataSourcesForAllHostsFromUrl(connectionsToAllHostsInCluster, readUrl, userName, password);
        }
        if (StringUtils.isNotBlank(cascadeAsyncReadUrl)) {
            addDataSourcesForAllHostsFromUrl(connectionsToAllHostsInCluster, cascadeAsyncReadUrl, userName, password);
        }
        final PgConnection connectionToPrimary = findConnectionToPrimary(connectionsToAllHostsInCluster);
        final Set<PgConnection> pgConnections = new HashSet<>(connectionsToAllHostsInCluster.values());
        return HighAvailabilityPgConnectionImpl.of(connectionToPrimary, pgConnections);
    }

    private void addDataSourcesForAllHostsFromUrl(@Nonnull final Map<String, PgConnection> connectionsToAllHostsInCluster,
                                                  @Nonnull final String anyUrl,
                                                  @Nonnull final String userName,
                                                  @Nonnull final String password) {
        final List<Pair<String, String>> allHosts = PgUrlParser.extractNameWithPortAndUrlForEachHost(anyUrl);
        for (Pair<String, String> host : allHosts) {
            connectionsToAllHostsInCluster.computeIfAbsent(
                    host.getKey(), h -> pgConnectionFactory.forUrl(host.getValue(), userName, password));
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
