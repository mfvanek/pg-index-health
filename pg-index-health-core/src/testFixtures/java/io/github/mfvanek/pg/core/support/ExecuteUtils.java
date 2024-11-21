/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.support;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.core.support.statements.DbStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public final class ExecuteUtils {

    private ExecuteUtils() {
        throw new UnsupportedOperationException();
    }

    public static void executeOnDatabase(@Nonnull final DataSource dataSource,
                                         @Nonnull final DbStatement callback) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            callback.execute(statement);
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }

    public static void executeInTransaction(@Nonnull final DataSource dataSource,
                                            @Nonnull final Collection<? extends DbStatement> dbStatements) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            for (final DbStatement dbStatement : dbStatements) {
                dbStatement.execute(statement);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }
}
