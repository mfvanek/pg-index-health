/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
        final DataSource dataSource = PgConnectionHelper.createDataSource(getWriteUrl(), "postgres", "postgres");
        assertThat(dataSource).isNotNull();
    }

    @Nonnull
    private String getWriteUrl() {
        return String.format("jdbc:postgresql://localhost:%d/postgres?prepareThreshold=0&preparedStatementCacheQueries=0", 6432);
    }
}
