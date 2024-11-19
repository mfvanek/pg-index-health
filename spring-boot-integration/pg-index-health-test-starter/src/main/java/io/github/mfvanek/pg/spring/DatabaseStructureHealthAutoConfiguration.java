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
import io.github.mfvanek.pg.exception.PgSqlException;
import io.github.mfvanek.pg.host.PgHost;
import io.github.mfvanek.pg.host.PgHostImpl;
import org.springframework.beans.factory.annotation.Qualifier;
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
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
     * @param dataSource  {@link DataSource} instance
     * @param databaseUrl connection string to database
     * @return {@link PgConnection} instance
     */
    @Bean
    @ConditionalOnBean(name = "dataSource")
    @ConditionalOnMissingBean
    public PgConnection pgConnection(@Qualifier("dataSource") final DataSource dataSource,
                                     @Value("${spring.datasource.url:#{null}}") final String databaseUrl) {
        final PgHost host;
        if (Objects.isNull(databaseUrl) || databaseUrl.isBlank()) {
            try (Connection connection = dataSource.getConnection()) {
                host = PgHostImpl.ofUrl(connection.getMetaData().getURL());
            } catch (SQLException ex) {
                throw new PgSqlException(ex);
            }
        } else {
            host = PgHostImpl.ofUrl(databaseUrl);
        }
        return PgConnectionImpl.of(dataSource, host);
    }
}
