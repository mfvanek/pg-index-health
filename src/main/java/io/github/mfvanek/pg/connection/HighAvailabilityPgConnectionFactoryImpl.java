/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    private HighAvailabilityPgConnection create(@Nonnull final String writeUrl,
                                                @Nonnull final String userName,
                                                @Nonnull final String password,
                                                @Nullable final String readUrl,
                                                @Nullable final String cascadeAsyncReadUrl) {
        final var connectionToMaster = pgConnectionFactory.forUrl(writeUrl, userName, password);
        final Map<String, PgConnection> connectionsToReplicas = new HashMap<>();
        addReplicasDataSources(connectionsToReplicas, writeUrl, userName, password);
        if (StringUtils.isNotBlank(readUrl)) {
            addReplicasDataSources(connectionsToReplicas, readUrl, userName, password);
        }
        if (StringUtils.isNotBlank(cascadeAsyncReadUrl)) {
            addReplicasDataSources(connectionsToReplicas, cascadeAsyncReadUrl, userName, password);
        }
        return HighAvailabilityPgConnectionImpl.of(connectionToMaster, Set.copyOf(connectionsToReplicas.values()));
    }

    private void addReplicasDataSources(@Nonnull final Map<String, PgConnection> connectionsToReplicas,
                                        @Nonnull final String readUrl,
                                        @Nonnull final String userName,
                                        @Nonnull final String password) {
        final var allHosts = PgUrlParser.extractNameWithPortAndUrlForEachHost(readUrl);
        for (var host : allHosts) {
            connectionsToReplicas.computeIfAbsent(
                    host.getKey(), h -> pgConnectionFactory.forUrl(host.getValue(), userName, password));
        }
    }
}
