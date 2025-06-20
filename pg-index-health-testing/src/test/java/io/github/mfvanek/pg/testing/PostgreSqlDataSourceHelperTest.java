/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.JdbcDatabaseContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Tag("fast")
class PostgreSqlDataSourceHelperTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(PostgreSqlDataSourceHelper.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void buildDataSourceShouldUseFieldsFromTestcontainer() {
        try (JdbcDatabaseContainer<?> container = Mockito.mock(JdbcDatabaseContainer.class)) {
            when(container.getJdbcUrl()).thenReturn("test url");
            when(container.getUsername()).thenReturn("some user name");
            when(container.getPassword()).thenReturn("test password");
            when(container.getDriverClassName()).thenReturn("some postgresql driver");

            assertThat(PostgreSqlDataSourceHelper.buildDataSource(container))
                .isNotNull()
                .satisfies(ds -> {
                    assertThat(ds.getUrl()).isEqualTo("test url");
                    assertThat(ds.getUserName()).isEqualTo("some user name");
                    assertThat(ds.getPassword()).isEqualTo("test password");
                    assertThat(ds.getDriverClassName()).isEqualTo("some postgresql driver");
                });
        }
    }
}
