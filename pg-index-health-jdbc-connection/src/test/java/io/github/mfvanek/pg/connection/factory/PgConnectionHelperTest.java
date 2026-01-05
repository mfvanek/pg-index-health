/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;
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
                assertThat(ds.getUserName()).isEqualTo("postgres_user");
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

    @NonNull
    private String getWriteUrl() {
        return String.format(Locale.ROOT, "jdbc:postgresql://localhost:%d/postgres?prepareThreshold=0&preparedStatementCacheQueries=0", 6432);
    }
}
