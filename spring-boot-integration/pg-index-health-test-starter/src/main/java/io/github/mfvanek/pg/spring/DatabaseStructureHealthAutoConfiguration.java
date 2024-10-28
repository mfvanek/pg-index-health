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

import io.github.mfvanek.pg.checks.host.BtreeIndexesOnArrayColumnsCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithJsonTypeCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.checks.host.ColumnsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.DuplicatedForeignKeysCheckOnHost;
import io.github.mfvanek.pg.checks.host.DuplicatedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.ForeignKeysNotCoveredWithIndexCheckOnHost;
import io.github.mfvanek.pg.checks.host.ForeignKeysWithUnmatchedColumnTypeCheckOnHost;
import io.github.mfvanek.pg.checks.host.FunctionsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithBloatCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithBooleanCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithNullValuesCheckOnHost;
import io.github.mfvanek.pg.checks.host.IntersectedForeignKeysCheckOnHost;
import io.github.mfvanek.pg.checks.host.IntersectedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.InvalidIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.NotValidConstraintsCheckOnHost;
import io.github.mfvanek.pg.checks.host.PossibleObjectNameOverflowCheckOnHost;
import io.github.mfvanek.pg.checks.host.PrimaryKeysWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.checks.host.SequenceOverflowCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesNotLinkedToOthersCheckOnHost;
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
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
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

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(DuplicatedIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public DuplicatedIndexesCheckOnHost duplicatedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new DuplicatedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(ForeignKeysNotCoveredWithIndexCheckOnHost.class)
    @ConditionalOnMissingBean
    public ForeignKeysNotCoveredWithIndexCheckOnHost foreignKeysNotCoveredWithIndexCheckOnHost(final PgConnection pgConnection) {
        return new ForeignKeysNotCoveredWithIndexCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(IndexesWithBloatCheckOnHost.class)
    @ConditionalOnMissingBean
    public IndexesWithBloatCheckOnHost indexesWithBloatCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithBloatCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(IndexesWithNullValuesCheckOnHost.class)
    @ConditionalOnMissingBean
    public IndexesWithNullValuesCheckOnHost indexesWithNullValuesCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithNullValuesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(IntersectedIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public IntersectedIndexesCheckOnHost intersectedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new IntersectedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(InvalidIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public InvalidIndexesCheckOnHost invalidIndexesCheckOnHost(final PgConnection pgConnection) {
        return new InvalidIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(TablesWithBloatCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithBloatCheckOnHost tablesWithBloatCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithBloatCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(TablesWithMissingIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithMissingIndexesCheckOnHost tablesWithMissingIndexesCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithMissingIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(TablesWithoutPrimaryKeyCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithoutPrimaryKeyCheckOnHost tablesWithoutPrimaryKeyCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithoutPrimaryKeyCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(UnusedIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public UnusedIndexesCheckOnHost unusedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new UnusedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(TablesWithoutDescriptionCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithoutDescriptionCheckOnHost tablesWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(ColumnsWithoutDescriptionCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsWithoutDescriptionCheckOnHost columnsWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(ColumnsWithJsonTypeCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsWithJsonTypeCheckOnHost columnsWithJsonTypeCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithJsonTypeCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(ColumnsWithSerialTypesCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsWithSerialTypesCheckOnHost columnsWithSerialTypesCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithSerialTypesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(FunctionsWithoutDescriptionCheckOnHost.class)
    @ConditionalOnMissingBean
    public FunctionsWithoutDescriptionCheckOnHost functionsWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new FunctionsWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(IndexesWithBooleanCheckOnHost.class)
    @ConditionalOnMissingBean
    public IndexesWithBooleanCheckOnHost indexesWithBooleanCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithBooleanCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(NotValidConstraintsCheckOnHost.class)
    @ConditionalOnMissingBean
    public NotValidConstraintsCheckOnHost notValidConstraintsCheckOnHost(final PgConnection pgConnection) {
        return new NotValidConstraintsCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(BtreeIndexesOnArrayColumnsCheckOnHost.class)
    @ConditionalOnMissingBean
    public BtreeIndexesOnArrayColumnsCheckOnHost btreeIndexesOnArrayColumnsCheckOnHost(final PgConnection pgConnection) {
        return new BtreeIndexesOnArrayColumnsCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(SequenceOverflowCheckOnHost.class)
    @ConditionalOnMissingBean
    public SequenceOverflowCheckOnHost sequenceOverflowCheckOnHost(final PgConnection pgConnection) {
        return new SequenceOverflowCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(PrimaryKeysWithSerialTypesCheckOnHost.class)
    @ConditionalOnMissingBean
    public PrimaryKeysWithSerialTypesCheckOnHost primaryKeysWithSerialTypesCheckOnHost(final PgConnection pgConnection) {
        return new PrimaryKeysWithSerialTypesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(DuplicatedForeignKeysCheckOnHost.class)
    @ConditionalOnMissingBean
    public DuplicatedForeignKeysCheckOnHost duplicatedForeignKeysCheckOnHost(final PgConnection pgConnection) {
        return new DuplicatedForeignKeysCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(IntersectedForeignKeysCheckOnHost.class)
    @ConditionalOnMissingBean
    public IntersectedForeignKeysCheckOnHost intersectedForeignKeysCheckOnHost(final PgConnection pgConnection) {
        return new IntersectedForeignKeysCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(PossibleObjectNameOverflowCheckOnHost.class)
    @ConditionalOnMissingBean
    public PossibleObjectNameOverflowCheckOnHost possibleObjectNameOverflowCheckOnHost(final PgConnection pgConnection) {
        return new PossibleObjectNameOverflowCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(TablesNotLinkedToOthersCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesNotLinkedToOthersCheckOnHost tablesNotLinkedToOthersCheckOnHost(final PgConnection pgConnection) {
        return new TablesNotLinkedToOthersCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(ForeignKeysWithUnmatchedColumnTypeCheckOnHost.class)
    @ConditionalOnMissingBean
    public ForeignKeysWithUnmatchedColumnTypeCheckOnHost foreignKeysWithUnmatchedColumnTypeCheckOnHost(final PgConnection pgConnection) {
        return new ForeignKeysWithUnmatchedColumnTypeCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(StatisticsMaintenanceOnHost.class)
    @ConditionalOnMissingBean
    public StatisticsMaintenanceOnHost statisticsMaintenanceOnHost(final PgConnection pgConnection) {
        return new StatisticsMaintenanceOnHostImpl(pgConnection);
    }

    @Bean
    @ConditionalOnBean(PgConnection.class)
    @ConditionalOnClass(ConfigurationMaintenanceOnHost.class)
    @ConditionalOnMissingBean
    public ConfigurationMaintenanceOnHost configurationMaintenanceOnHost(final PgConnection pgConnection) {
        return new ConfigurationMaintenanceOnHostImpl(pgConnection);
    }
}
