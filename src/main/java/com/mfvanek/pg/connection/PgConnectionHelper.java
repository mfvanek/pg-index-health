/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

// TODO Add tests
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
        final var dataSource = new BasicDataSource();
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
