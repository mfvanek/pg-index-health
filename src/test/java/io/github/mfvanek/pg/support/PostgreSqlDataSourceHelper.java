/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import org.apache.commons.dbcp2.BasicDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * Builder for DataSource.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.2
 */
public final class PostgreSqlDataSourceHelper {

    private PostgreSqlDataSourceHelper() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static DataSource buildDataSource(@Nonnull final JdbcDatabaseContainer<?> container) {
        final BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(container.getJdbcUrl());
        basicDataSource.setUsername(container.getUsername());
        basicDataSource.setPassword(container.getPassword());
        basicDataSource.setDriverClassName(container.getDriverClassName());
        return basicDataSource;
    }
}
