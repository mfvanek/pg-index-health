/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * Autoconfiguration for using pg-index-health in component/integration tests.
 *
 * @author Ivan Vakhrushev
 * @since 0.3.1
 */
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DatabaseStructureHealthProperties.class)
@ConditionalOnClass(value = DataSource.class, name = "org.postgresql.Driver")
@Conditional(DatabaseStructureHealthCondition.class)
@ConditionalOnProperty(name = "pg.index.health.test.enabled", matchIfMissing = true, havingValue = "true")
public class DatabaseStructureHealthAutoConfiguration {

    /**
     * Configuration properties for the database structure health check.
     */
    private final DatabaseStructureHealthProperties properties;

    /**
     * Constructs a new instance of {@code DatabaseStructureHealthAutoConfiguration}.
     *
     * @param properties the {@link DatabaseStructureHealthProperties} containing
     *                   the configuration for this auto-configuration (must not be null)
     */
    public DatabaseStructureHealthAutoConfiguration(@Nonnull final DatabaseStructureHealthProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates and configures a {@link PgConnection} bean.
     * <p>
     * This bean is created only if:
     * <ul>
     * <li>A {@link DataSource} bean is available in the application context.</li>
     * <li>No other {@link PgConnection} bean is already defined.</li>
     * </ul>
     * The {@link DataSource} bean and database URL property are resolved dynamically
     * based on the configured {@link DatabaseStructureHealthProperties}.
     *
     * @param beanFactory the {@link BeanFactory} instance used to retrieve the {@link DataSource} bean
     * @param environment the {@link Environment} instance used to resolve the database URL property
     * @return a configured {@link PgConnection} instance
     */
    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean
    public PgConnection pgConnection(@Nonnull final BeanFactory beanFactory,
                                     @Nonnull final Environment environment) {
        final DataSource dataSource = beanFactory.getBean(properties.getDatasourceBeanName(), DataSource.class);
        final String databaseUrl = environment.getProperty(properties.getDatasourceUrlPropertyName());
        return PgConnectionImpl.ofUrl(dataSource, databaseUrl);
    }
}
