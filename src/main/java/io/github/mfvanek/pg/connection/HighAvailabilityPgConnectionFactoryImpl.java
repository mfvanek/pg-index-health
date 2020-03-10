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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HighAvailabilityPgConnectionFactoryImpl implements HighAvailabilityPgConnectionFactory {

    private final PgConnectionFactory pgConnectionFactory;

    public HighAvailabilityPgConnectionFactoryImpl(@Nonnull final PgConnectionFactory pgConnectionFactory) {
        this.pgConnectionFactory = Objects.requireNonNull(pgConnectionFactory);
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
        final PgConnection connectionToMaster = pgConnectionFactory.forUrl(writeUrl, userName, password);
        final Map<String, PgConnection> connectionsToAllHostsInCluster = new HashMap<>();
        addDataSourcesForAllHostsFromUrl(connectionsToAllHostsInCluster, writeUrl, userName, password);
        if (StringUtils.isNotBlank(readUrl)) {
            addDataSourcesForAllHostsFromUrl(connectionsToAllHostsInCluster, readUrl, userName, password);
        }
        if (StringUtils.isNotBlank(cascadeAsyncReadUrl)) {
            addDataSourcesForAllHostsFromUrl(connectionsToAllHostsInCluster, cascadeAsyncReadUrl, userName, password);
        }
        return HighAvailabilityPgConnectionImpl.of(connectionToMaster, new HashSet<>(connectionsToAllHostsInCluster.values()));
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
}
