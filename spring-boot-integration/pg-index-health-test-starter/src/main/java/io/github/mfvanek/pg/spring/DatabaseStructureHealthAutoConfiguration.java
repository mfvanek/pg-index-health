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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

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
     * {@link PgConnection} bean.
     *
     * @param beanFactory {@link BeanFactory} instance
     * @param databaseUrl connection string to database
     * @return {@link PgConnection} instance
     */
    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean
    public PgConnection pgConnection(@Nonnull final BeanFactory beanFactory,
                                     @Value("${spring.datasource.url:#{null}}") final String databaseUrl) {
        // TODO
        final DataSource dataSource = beanFactory.getBean("dataSource", DataSource.class);
        return PgConnectionImpl.ofUrl(dataSource, databaseUrl);
    }
}
