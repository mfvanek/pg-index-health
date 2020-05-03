/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import com.vdurmont.semver4j.Semver;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PostgresVersionTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final static String PG_VERSION_ENVIRONMENT_VARIABLE = "TEST_PG_VERSION";

    PostgresVersionTest() {
        super(embeddedPostgres.getTestDatabase());
    }

    @DisplayName("PostgreSQL version is the same as specified in environment variable " + PG_VERSION_ENVIRONMENT_VARIABLE)
    @Test
    void checkPgVersion() {
        String requiredPgVersionString = System.getenv(PG_VERSION_ENVIRONMENT_VARIABLE);
        if (requiredPgVersionString == null) {
            requiredPgVersionString = "10.11.0";
        }
        Semver actualPgVersion = new Semver(readPgVersion(), Semver.SemverType.LOOSE);
        Semver requiredPgVersion = new Semver(requiredPgVersionString);
        assertEquals(requiredPgVersion.getMajor(), actualPgVersion.getMajor());
        assertEquals(requiredPgVersion.getMinor(), actualPgVersion.getMinor());
        if (actualPgVersion.getPatch() == null) {
            assertEquals(0, requiredPgVersion.getPatch());
        } else {
            assertEquals(requiredPgVersion.getPatch(), actualPgVersion.getPatch());
        }
    }

    @Nonnull
    private String readPgVersion() {
        try (Connection connection = embeddedPostgres.getTestDatabase().getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("show server_version")) {
                resultSet.next();
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
