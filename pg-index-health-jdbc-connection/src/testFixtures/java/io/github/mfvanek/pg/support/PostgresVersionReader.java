/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import io.github.mfvanek.pg.connection.PgSqlException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public final class PostgresVersionReader {

    private PostgresVersionReader() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static String readVersion(@Nonnull final DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("show server_version")) {
                resultSet.next();
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }
}
