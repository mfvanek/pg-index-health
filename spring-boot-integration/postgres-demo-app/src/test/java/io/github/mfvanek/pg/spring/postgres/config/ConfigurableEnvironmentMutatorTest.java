/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.testcontainers.containers.JdbcDatabaseContainer;

import static io.github.mfvanek.pg.spring.postgres.config.ConfigurableEnvironmentMutator.DATASOURCE_URL_PROP_NAME;
import static org.assertj.core.api.Assertions.assertThat;

class ConfigurableEnvironmentMutatorTest {

    private final JdbcDatabaseContainer<?> jdbcDatabaseContainer = Mockito.mock(JdbcDatabaseContainer.class);

    @Test
    void shouldNotAddPropIfExist() {
        final MockEnvironment environment = new MockEnvironment();
        environment.setProperty(DATASOURCE_URL_PROP_NAME, "url");

        assertThat(ConfigurableEnvironmentMutator.addDatasourceUrlIfNeed(jdbcDatabaseContainer, environment))
            .isFalse();
        assertThat(environment.getProperty(DATASOURCE_URL_PROP_NAME)).isEqualTo("url");
    }

    @Test
    void shouldNotAddPropIfInvalidType() {
        final Environment environment = Mockito.mock(Environment.class);
        Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn(null);

        assertThat(ConfigurableEnvironmentMutator.addDatasourceUrlIfNeed(jdbcDatabaseContainer, environment))
            .isFalse();
    }

    @Test
    void shouldAddProperty() {
        final MockEnvironment environment = new MockEnvironment();
        Mockito.when(jdbcDatabaseContainer.getJdbcUrl()).thenReturn("added_url");

        assertThat(ConfigurableEnvironmentMutator.addDatasourceUrlIfNeed(jdbcDatabaseContainer, environment))
            .isTrue();
        assertThat(environment.getProperty(DATASOURCE_URL_PROP_NAME)).isEqualTo("added_url");
    }
}
