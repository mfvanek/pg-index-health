/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.PostgresVersionReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class PostgresVersionTest extends DatabaseAwareTestBase {

    private static final String PG_VERSION_ENVIRONMENT_VARIABLE = "TEST_PG_VERSION";

    @DisplayName("PostgreSQL version is the same as specified in environment variable " + PG_VERSION_ENVIRONMENT_VARIABLE)
    @Test
    void checkPgVersion() {
        final String pgVersionFromEnv = System.getenv(PG_VERSION_ENVIRONMENT_VARIABLE);
        final String requiredPgVersionString = (pgVersionFromEnv == null) ? "16.2 (Debian 16.2-" : pgVersionFromEnv.split("-")[0];
        final String actualPgVersionString = PostgresVersionReader.readVersion(getDataSource());
        assertThat(actualPgVersionString)
                .startsWith(requiredPgVersionString);
    }
}
