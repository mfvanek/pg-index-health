/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgConnectionHelperTest {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(PgConnectionHelper.class)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void createDataSource() {
        final DataSource dataSource = PgConnectionHelper.createDataSource(getWriteUrl(), "postgres", "postgres");
        assertThat(dataSource).isNotNull();
    }

    @Nonnull
    private String getWriteUrl() {
        final int port = embeddedPostgres.getPort();
        return String.format(
                "jdbc:postgresql://localhost:%d/postgres?prepareThreshold=0&preparedStatementCacheQueries=0",
                port);
    }
}
