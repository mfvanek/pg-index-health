/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.config;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.Map;
import javax.annotation.Nonnull;

@UtilityClass
class ConfigurableEnvironmentMutator {

    static final String DATASOURCE_URL_PROP_NAME = "spring.datasource.url";

    static boolean addDatasourceUrlIfNeed(@Nonnull final JdbcDatabaseContainer<?> jdbcDatabaseContainer,
                                          @Nonnull final Environment environment) {
        if (environment.getProperty(DATASOURCE_URL_PROP_NAME) == null && environment instanceof ConfigurableEnvironment) {
            final ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
            final MutablePropertySources mps = configurableEnvironment.getPropertySources();
            mps.addFirst(new MapPropertySource("connectionString",
                Map.ofEntries(Map.entry(DATASOURCE_URL_PROP_NAME, jdbcDatabaseContainer.getJdbcUrl()))));
            return true;
        }
        return false;
    }
}
