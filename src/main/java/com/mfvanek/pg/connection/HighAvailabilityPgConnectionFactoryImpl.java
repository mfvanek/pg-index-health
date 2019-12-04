/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// TODO add tests for wrong params
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
        final var connectionToMaster = pgConnectionFactory.forUrl(writeUrl, userName, password);
        return HighAvailabilityPgConnectionImpl.of(connectionToMaster);
    }

    @Override
    @Nonnull
    public HighAvailabilityPgConnection of(@Nonnull final String writeUrl,
                                           @Nonnull final String userName,
                                           @Nonnull final String password,
                                           @Nonnull final String readUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(readUrl, "readUrl");
        final var connectionToMaster = pgConnectionFactory.forUrl(writeUrl, userName, password);
        final Map<String, PgConnection> connectionsToReplicas = new HashMap<>();
        addReplicasDataSources(connectionsToReplicas, readUrl, userName, password);
        return HighAvailabilityPgConnectionImpl.of(connectionToMaster, Set.copyOf(connectionsToReplicas.values()));
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
        final var connectionToMaster = pgConnectionFactory.forUrl(writeUrl, userName, password);
        final Map<String, PgConnection> connectionsToReplicas = new HashMap<>();
        addReplicasDataSources(connectionsToReplicas, readUrl, userName, password);
        addReplicasDataSources(connectionsToReplicas, cascadeAsyncReadUrl, userName, password);
        return HighAvailabilityPgConnectionImpl.of(connectionToMaster, Set.copyOf(connectionsToReplicas.values()));
    }

    private void addReplicasDataSources(@Nonnull final Map<String, PgConnection> connectionsToReplicas,
                                        @Nonnull final String readUrl,
                                        @Nonnull final String userName,
                                        @Nonnull final String password) {
        final var allHosts = PgUrlParser.extractNamesAndUrlsForEachHost(readUrl);
        for (var host : allHosts) {
            connectionsToReplicas.computeIfAbsent(
                    host.getKey(), h -> pgConnectionFactory.forUrl(host.getValue(), userName, password));
        }
    }
}
