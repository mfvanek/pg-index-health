/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import org.apache.commons.dbcp2.BasicDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;

/**
 * Builder for DataSource.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.2
 */
final class PostgreSqlDataSourceHelper {

    private PostgreSqlDataSourceHelper() {
        throw new UnsupportedOperationException();
    }

    static BasicDataSource buildDataSource(final JdbcDatabaseContainer<?> container) {
        final BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(container.getJdbcUrl());
        basicDataSource.setUsername(container.getUsername());
        basicDataSource.setPassword(container.getPassword());
        basicDataSource.setDriverClassName(container.getDriverClassName());
        return basicDataSource;
    }
}
