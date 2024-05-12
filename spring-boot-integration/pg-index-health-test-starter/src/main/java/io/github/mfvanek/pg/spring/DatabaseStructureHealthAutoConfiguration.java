/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import io.github.mfvanek.pg.checks.host.BtreeIndexesOnArrayColumnsCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithJsonTypeCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.DuplicatedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.ForeignKeysNotCoveredWithIndexCheckOnHost;
import io.github.mfvanek.pg.checks.host.FunctionsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithBloatCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithBooleanCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithNullValuesCheckOnHost;
import io.github.mfvanek.pg.checks.host.IntersectedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.InvalidIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.NotValidConstraintsCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithBloatCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithMissingIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithoutPrimaryKeyCheckOnHost;
import io.github.mfvanek.pg.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.connection.PgHostImpl;
import io.github.mfvanek.pg.connection.PgSqlException;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHostImpl;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHostImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
 * Autoconfiguration for using pg-index-health in unit tests.
 *
 * @author Ivan Vakhrushev
 * @since 0.3.1
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
@AutoConfiguration
@EnableConfigurationProperties(DatabaseStructureHealthProperties.class)
@ConditionalOnClass(value = DataSource.class, name = "org.postgresql.Driver")
@Conditional(DatabaseStructureHealthCondition.class)
@ConditionalOnProperty(name = "pg.index.health.test.enabled", matchIfMissing = true, havingValue = "true")
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class DatabaseStructureHealthAutoConfiguration {

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

    @Bean
    @ConditionalOnClass(DuplicatedIndexesCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public DuplicatedIndexesCheckOnHost duplicatedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new DuplicatedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ForeignKeysNotCoveredWithIndexCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public ForeignKeysNotCoveredWithIndexCheckOnHost foreignKeysNotCoveredWithIndexCheckOnHost(final PgConnection pgConnection) {
        return new ForeignKeysNotCoveredWithIndexCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IndexesWithBloatCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public IndexesWithBloatCheckOnHost indexesWithBloatCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithBloatCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IndexesWithNullValuesCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public IndexesWithNullValuesCheckOnHost indexesWithNullValuesCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithNullValuesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IntersectedIndexesCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public IntersectedIndexesCheckOnHost intersectedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new IntersectedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(InvalidIndexesCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public InvalidIndexesCheckOnHost invalidIndexesCheckOnHost(final PgConnection pgConnection) {
        return new InvalidIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithBloatCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public TablesWithBloatCheckOnHost tablesWithBloatCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithBloatCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithMissingIndexesCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public TablesWithMissingIndexesCheckOnHost tablesWithMissingIndexesCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithMissingIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithoutPrimaryKeyCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public TablesWithoutPrimaryKeyCheckOnHost tablesWithoutPrimaryKeyCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithoutPrimaryKeyCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(UnusedIndexesCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public UnusedIndexesCheckOnHost unusedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new UnusedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithoutDescriptionCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public TablesWithoutDescriptionCheckOnHost tablesWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsWithoutDescriptionCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public ColumnsWithoutDescriptionCheckOnHost columnsWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsWithJsonTypeCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public ColumnsWithJsonTypeCheckOnHost columnsWithJsonTypeCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithJsonTypeCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsWithSerialTypesCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public ColumnsWithSerialTypesCheckOnHost columnsWithSerialTypesCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithSerialTypesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(FunctionsWithoutDescriptionCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public FunctionsWithoutDescriptionCheckOnHost functionsWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new FunctionsWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IndexesWithBooleanCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public IndexesWithBooleanCheckOnHost indexesWithBooleanCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithBooleanCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(NotValidConstraintsCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public NotValidConstraintsCheckOnHost notValidConstraintsCheckOnHost(final PgConnection pgConnection) {
        return new NotValidConstraintsCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(BtreeIndexesOnArrayColumnsCheckOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public BtreeIndexesOnArrayColumnsCheckOnHost btreeIndexesOnArrayColumnsCheckOnHost(final PgConnection pgConnection) {
        return new BtreeIndexesOnArrayColumnsCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(StatisticsMaintenanceOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public StatisticsMaintenanceOnHost statisticsMaintenanceOnHost(final PgConnection pgConnection) {
        return new StatisticsMaintenanceOnHostImpl(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ConfigurationMaintenanceOnHost.class)
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnMissingBean
    public ConfigurationMaintenanceOnHost configurationMaintenanceOnHost(final PgConnection pgConnection) {
        return new ConfigurationMaintenanceOnHostImpl(pgConnection);
    }
}
