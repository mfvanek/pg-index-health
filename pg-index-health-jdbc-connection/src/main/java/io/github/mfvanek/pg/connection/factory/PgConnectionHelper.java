/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.validation.PgConnectionValidators;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

final class PgConnectionHelper {

    private PgConnectionHelper() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    static DataSource createDataSource(@Nonnull final String pgUrl,
                                       @Nonnull final String userName,
                                       @Nonnull final String password) {
        PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        PgConnectionValidators.userNameNotBlank(userName);
        PgConnectionValidators.passwordNotBlank(password);
        final BasicDataSource dataSource = new BasicDataSource();
        setCommonProperties(dataSource, userName, password);
        dataSource.setUrl(pgUrl);
        return dataSource;
    }

    private static void setCommonProperties(@Nonnull final BasicDataSource dataSource,
                                            @Nonnull final String userName,
                                            @Nonnull final String password) {
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("select 1");
        dataSource.setMaxTotal(1);
        dataSource.setMaxIdle(1);
        dataSource.setMaxOpenPreparedStatements(1);
    }
}
