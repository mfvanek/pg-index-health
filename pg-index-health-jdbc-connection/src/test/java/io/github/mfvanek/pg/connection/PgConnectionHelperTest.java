/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.support.TestUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class PgConnectionHelperTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(PgConnectionHelper.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void createDataSource() {
        final DataSource dataSource = PgConnectionHelper.createDataSource(getWriteUrl(), "postgres_user", "postgres_pwd");
        assertThat(dataSource)
            .isNotNull()
            .isInstanceOfSatisfying(BasicDataSource.class, ds -> {
                assertThat(ds.getDriverClassName()).isEqualTo("org.postgresql.Driver");
                assertThat(ds.getUsername()).isEqualTo("postgres_user");
                assertThat(ds.getPassword()).isEqualTo("postgres_pwd");
                assertThat(ds.getValidationQuery()).isEqualTo("select 1");
                assertThat(ds.getMaxTotal()).isEqualTo(1);
                assertThat(ds.getMaxIdle()).isEqualTo(1);
                assertThat(ds.getMaxOpenPreparedStatements()).isEqualTo(1);
                assertThat(ds.getUrl())
                    .isNotBlank()
                    .startsWith("jdbc:postgresql://localhost");
            });
    }

    @Nonnull
    private String getWriteUrl() {
        return String.format(Locale.ROOT, "jdbc:postgresql://localhost:%d/postgres?prepareThreshold=0&preparedStatementCacheQueries=0", 6432);
    }
}
