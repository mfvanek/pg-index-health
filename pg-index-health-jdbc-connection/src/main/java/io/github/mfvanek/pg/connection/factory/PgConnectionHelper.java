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

import io.github.mfvanek.pg.connection.host.PgUrlValidators;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

final class PgConnectionHelper {

    private PgConnectionHelper() {
        throw new UnsupportedOperationException();
    }

    static DataSource createDataSource(final String pgUrl,
                                       final String userName,
                                       final String password) {
        PgUrlValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        PgConnectionValidators.userNameNotBlank(userName);
        PgConnectionValidators.passwordNotBlank(password);
        final BasicDataSource dataSource = new BasicDataSource();
        setCommonProperties(dataSource, userName, password);
        dataSource.setUrl(pgUrl);
        return dataSource;
    }

    private static void setCommonProperties(final BasicDataSource dataSource,
                                            final String userName,
                                            final String password) {
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("select 1");
        dataSource.setMaxTotal(1);
        dataSource.setMaxIdle(1);
        dataSource.setMaxOpenPreparedStatements(1);
    }
}
