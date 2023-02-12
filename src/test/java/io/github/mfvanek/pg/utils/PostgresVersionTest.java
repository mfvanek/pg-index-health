/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

final class PostgresVersionTest extends DatabaseAwareTestBase {

    private static final String PG_VERSION_ENVIRONMENT_VARIABLE = "TEST_PG_VERSION";

    @DisplayName("PostgreSQL version is the same as specified in environment variable " + PG_VERSION_ENVIRONMENT_VARIABLE)
    @Test
    void checkPgVersion() {
        String requiredPgVersionString = System.getenv(PG_VERSION_ENVIRONMENT_VARIABLE);
        if (requiredPgVersionString == null) {
            requiredPgVersionString = "15.2 (Debian 15.2-";
        }
        final String actualPgVersionString = readPgVersion();
        assertThat(actualPgVersionString).startsWith(requiredPgVersionString);
    }

    @Nonnull
    private String readPgVersion() {
        try (Connection connection = getDataSource().getConnection();
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
