/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestUtils {

    public static <T> void invokePrivateConstructor(@Nonnull final Class<T> type)
            throws Throwable {
        final Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);

        try {
            constructor.newInstance();
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    public static void executeOnDatabase(@Nonnull final DataSource dataSource,
                                         @Nonnull DBCallback callback) {
        try (Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            callback.execute(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeInTransaction(@Nonnull final DataSource dataSource,
                                            @Nonnull DBCallback callback) {
        try (Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            callback.execute(statement);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
