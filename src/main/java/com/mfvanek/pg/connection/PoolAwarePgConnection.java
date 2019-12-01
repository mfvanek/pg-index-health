/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PoolAwarePgConnection implements PgConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolAwarePgConnection.class);

    private final BasicDataSource masterDataSource;
    private final Map<String, BasicDataSource> replicasDataSource;

    private PoolAwarePgConnection(@Nonnull final String writeUrl,
                                  @Nonnull final String userName,
                                  @Nonnull final String password,
                                  @Nullable final String readUrl,
                                  @Nullable final String cascadeAsyncReadUrl) {
        LOGGER.debug("Creating PoolAwarePgConnection with writeUrl = {}, userName = {}, " +
                        "password = {}, readUrl = {}, cascadeAsyncReadUrl = {}",
                writeUrl, userName, "*****", readUrl, cascadeAsyncReadUrl);
        Validators.pgUrlNotBlankAndValid(writeUrl, "writeUrl");
        Validators.userNameNotBlank(userName);
        Validators.passwordNotBlank(password);
        this.masterDataSource = new BasicDataSource();
        setCommonProperties(masterDataSource, userName, password)
                .setUrl(writeUrl);
        this.replicasDataSource = new HashMap<>();
        if (StringUtils.isNotBlank(readUrl)) {
            addReplicasDataSources(readUrl, userName, password);
        }
        if (StringUtils.isNotBlank(cascadeAsyncReadUrl)) {
            addReplicasDataSources(cascadeAsyncReadUrl, userName, password);
        }
    }

    private void addReplicasDataSources(@Nonnull final String readUrl,
                                        @Nonnull final String userName,
                                        @Nonnull final String password) {
        final var allHosts = PgUrlParser.extractNamesAndUrlsForEachHost(readUrl);
        for (var host : allHosts) {
            replicasDataSource.computeIfAbsent(host.getKey(), h -> {
                final BasicDataSource dataSource = new BasicDataSource();
                setCommonProperties(dataSource, userName, password)
                        .setUrl(host.getValue());
                return dataSource;
            });
        }
    }

    private static BasicDataSource setCommonProperties(@Nonnull final BasicDataSource dataSource,
                                                       @Nonnull final String userName,
                                                       @Nonnull final String password) {
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("select 1");
        dataSource.setMaxTotal(1);
        dataSource.setMaxIdle(1);
        dataSource.setMaxOpenPreparedStatements(1);
        return dataSource;
    }

    @Override
    @Nonnull
    public DataSource getMasterDataSource() {
        return masterDataSource;
    }

    @Override
    @Nonnull
    public List<? extends DataSource> getReplicasDataSource() {
        return List.copyOf(replicasDataSource.values());
    }

    @Override
    public int getReplicasCount() {
        return replicasDataSource.size();
    }

    public static PgConnection of(@Nonnull final String writeUrl,
                                  @Nonnull final String userName,
                                  @Nonnull final String password) {
        return new PoolAwarePgConnection(writeUrl, userName, password, null, null);
    }

    public static PgConnection of(@Nonnull final String writeUrl,
                                  @Nonnull final String userName,
                                  @Nonnull final String password,
                                  @Nonnull final String readUrl) {
        Validators.pgUrlNotBlankAndValid(readUrl, "readUrl");
        return new PoolAwarePgConnection(writeUrl, userName, password, readUrl, null);
    }

    public static PgConnection of(@Nonnull final String writeUrl,
                                  @Nonnull final String userName,
                                  @Nonnull final String password,
                                  @Nonnull final String readUrl,
                                  @Nonnull final String cascadeAsyncReadUrl) {
        Validators.pgUrlNotBlankAndValid(readUrl, "readUrl");
        Validators.pgUrlNotBlankAndValid(cascadeAsyncReadUrl, "cascadeAsyncReadUrl");
        return new PoolAwarePgConnection(writeUrl, userName, password, readUrl, cascadeAsyncReadUrl);
    }

    private static class Validators {

        private Validators() {
            throw new UnsupportedOperationException();
        }

        static void pgUrlNotBlankAndValid(@Nonnull final String pgUrl, @Nonnull final String argumentName) {
            notBlank(pgUrl, argumentName);
            if (!Objects.requireNonNull(pgUrl).startsWith("jdbc:postgresql://")) {
                throw new IllegalArgumentException(argumentName + " has invalid format");
            }
        }

        static void userNameNotBlank(@Nonnull final String userName) {
            notBlank(userName, "userName");
        }

        static void passwordNotBlank(@Nonnull final String password) {
            notBlank(password, "password");
        }

        private static void notBlank(@Nonnull final String argumentValue, @Nonnull final String argumentName) {
            if (StringUtils.isBlank(Objects.requireNonNull(argumentValue, argumentName + " cannot be null"))) {
                throw new IllegalArgumentException(argumentName);
            }
        }
    }
}
