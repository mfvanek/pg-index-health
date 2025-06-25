/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.host.BtreeIndexesOnArrayColumnsCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsNotFollowingNamingConventionCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithFixedLengthVarcharCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithJsonTypeCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithMoneyTypeCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.DuplicatedForeignKeysCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.DuplicatedIndexesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ForeignKeysNotCoveredWithIndexCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ForeignKeysWithUnmatchedColumnTypeCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.FunctionsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.IndexesWithBloatCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.IndexesWithBooleanCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.IndexesWithNullValuesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.IndexesWithTimestampInTheMiddleCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.IndexesWithUnnecessaryWhereClauseCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.IntersectedForeignKeysCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.IntersectedIndexesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.InvalidIndexesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.NotValidConstraintsCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ObjectsNotFollowingNamingConventionCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.PossibleObjectNameOverflowCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.PrimaryKeysWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.PrimaryKeysWithVarcharCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.SequenceOverflowCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesNotLinkedToOthersCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithBloatCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithMissingIndexesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithZeroOrOneColumnCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithoutPrimaryKeyCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHostImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Autoconfiguration for database checks.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.2
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
@AutoConfiguration(after = DatabaseStructureHealthAutoConfiguration.class)
@ConditionalOnBean(PgConnection.class)
public class DatabaseStructureChecksAutoConfiguration {

    @Bean
    @ConditionalOnClass(DuplicatedIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public DuplicatedIndexesCheckOnHost duplicatedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new DuplicatedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ForeignKeysNotCoveredWithIndexCheckOnHost.class)
    @ConditionalOnMissingBean
    public ForeignKeysNotCoveredWithIndexCheckOnHost foreignKeysNotCoveredWithIndexCheckOnHost(final PgConnection pgConnection) {
        return new ForeignKeysNotCoveredWithIndexCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IndexesWithBloatCheckOnHost.class)
    @ConditionalOnMissingBean
    public IndexesWithBloatCheckOnHost indexesWithBloatCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithBloatCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IndexesWithNullValuesCheckOnHost.class)
    @ConditionalOnMissingBean
    public IndexesWithNullValuesCheckOnHost indexesWithNullValuesCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithNullValuesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IntersectedIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public IntersectedIndexesCheckOnHost intersectedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new IntersectedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(InvalidIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public InvalidIndexesCheckOnHost invalidIndexesCheckOnHost(final PgConnection pgConnection) {
        return new InvalidIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithBloatCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithBloatCheckOnHost tablesWithBloatCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithBloatCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithMissingIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithMissingIndexesCheckOnHost tablesWithMissingIndexesCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithMissingIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithoutPrimaryKeyCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithoutPrimaryKeyCheckOnHost tablesWithoutPrimaryKeyCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithoutPrimaryKeyCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(UnusedIndexesCheckOnHost.class)
    @ConditionalOnMissingBean
    public UnusedIndexesCheckOnHost unusedIndexesCheckOnHost(final PgConnection pgConnection) {
        return new UnusedIndexesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithoutDescriptionCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithoutDescriptionCheckOnHost tablesWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsWithoutDescriptionCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsWithoutDescriptionCheckOnHost columnsWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsWithJsonTypeCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsWithJsonTypeCheckOnHost columnsWithJsonTypeCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithJsonTypeCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsWithSerialTypesCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsWithSerialTypesCheckOnHost columnsWithSerialTypesCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithSerialTypesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(FunctionsWithoutDescriptionCheckOnHost.class)
    @ConditionalOnMissingBean
    public FunctionsWithoutDescriptionCheckOnHost functionsWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        return new FunctionsWithoutDescriptionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IndexesWithBooleanCheckOnHost.class)
    @ConditionalOnMissingBean
    public IndexesWithBooleanCheckOnHost indexesWithBooleanCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithBooleanCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(NotValidConstraintsCheckOnHost.class)
    @ConditionalOnMissingBean
    public NotValidConstraintsCheckOnHost notValidConstraintsCheckOnHost(final PgConnection pgConnection) {
        return new NotValidConstraintsCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(BtreeIndexesOnArrayColumnsCheckOnHost.class)
    @ConditionalOnMissingBean
    public BtreeIndexesOnArrayColumnsCheckOnHost btreeIndexesOnArrayColumnsCheckOnHost(final PgConnection pgConnection) {
        return new BtreeIndexesOnArrayColumnsCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(SequenceOverflowCheckOnHost.class)
    @ConditionalOnMissingBean
    public SequenceOverflowCheckOnHost sequenceOverflowCheckOnHost(final PgConnection pgConnection) {
        return new SequenceOverflowCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(PrimaryKeysWithSerialTypesCheckOnHost.class)
    @ConditionalOnMissingBean
    public PrimaryKeysWithSerialTypesCheckOnHost primaryKeysWithSerialTypesCheckOnHost(final PgConnection pgConnection) {
        return new PrimaryKeysWithSerialTypesCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(DuplicatedForeignKeysCheckOnHost.class)
    @ConditionalOnMissingBean
    public DuplicatedForeignKeysCheckOnHost duplicatedForeignKeysCheckOnHost(final PgConnection pgConnection) {
        return new DuplicatedForeignKeysCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IntersectedForeignKeysCheckOnHost.class)
    @ConditionalOnMissingBean
    public IntersectedForeignKeysCheckOnHost intersectedForeignKeysCheckOnHost(final PgConnection pgConnection) {
        return new IntersectedForeignKeysCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(PossibleObjectNameOverflowCheckOnHost.class)
    @ConditionalOnMissingBean
    public PossibleObjectNameOverflowCheckOnHost possibleObjectNameOverflowCheckOnHost(final PgConnection pgConnection) {
        return new PossibleObjectNameOverflowCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesNotLinkedToOthersCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesNotLinkedToOthersCheckOnHost tablesNotLinkedToOthersCheckOnHost(final PgConnection pgConnection) {
        return new TablesNotLinkedToOthersCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ForeignKeysWithUnmatchedColumnTypeCheckOnHost.class)
    @ConditionalOnMissingBean
    public ForeignKeysWithUnmatchedColumnTypeCheckOnHost foreignKeysWithUnmatchedColumnTypeCheckOnHost(final PgConnection pgConnection) {
        return new ForeignKeysWithUnmatchedColumnTypeCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(TablesWithZeroOrOneColumnCheckOnHost.class)
    @ConditionalOnMissingBean
    public TablesWithZeroOrOneColumnCheckOnHost tablesWithZeroOrOneColumnCheckOnHost(final PgConnection pgConnection) {
        return new TablesWithZeroOrOneColumnCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ObjectsNotFollowingNamingConventionCheckOnHost.class)
    @ConditionalOnMissingBean
    public ObjectsNotFollowingNamingConventionCheckOnHost objectsNotFollowingNamingConventionCheckOnHost(final PgConnection pgConnection) {
        return new ObjectsNotFollowingNamingConventionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsNotFollowingNamingConventionCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsNotFollowingNamingConventionCheckOnHost columnsNotFollowingNamingConventionCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsNotFollowingNamingConventionCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(PrimaryKeysWithVarcharCheckOnHost.class)
    @ConditionalOnMissingBean
    public PrimaryKeysWithVarcharCheckOnHost primaryKeysWithVarcharCheckOnHost(final PgConnection pgConnection) {
        return new PrimaryKeysWithVarcharCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsWithFixedLengthVarcharCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsWithFixedLengthVarcharCheckOnHost columnsWithFixedLengthVarcharCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithFixedLengthVarcharCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IndexesWithUnnecessaryWhereClauseCheckOnHost.class)
    @ConditionalOnMissingBean
    public IndexesWithUnnecessaryWhereClauseCheckOnHost indexesWithUnnecessaryWhereClauseCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithUnnecessaryWhereClauseCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost.class)
    @ConditionalOnMissingBean
    public PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost primaryKeysThatMostLikelyNaturalKeysCheckOnHost(final PgConnection pgConnection) {
        return new PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(ColumnsWithMoneyTypeCheckOnHost.class)
    @ConditionalOnMissingBean
    public ColumnsWithMoneyTypeCheckOnHost columnsWithMoneyTypeCheckOnHost(final PgConnection pgConnection) {
        return new ColumnsWithMoneyTypeCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(IndexesWithTimestampInTheMiddleCheckOnHost.class)
    @ConditionalOnMissingBean
    public IndexesWithTimestampInTheMiddleCheckOnHost indexesWithTimestampInTheMiddleCheckOnHost(final PgConnection pgConnection) {
        return new IndexesWithTimestampInTheMiddleCheckOnHost(pgConnection);
    }

    @Bean
    @ConditionalOnClass(StatisticsMaintenanceOnHost.class)
    @ConditionalOnMissingBean
    public StatisticsMaintenanceOnHost statisticsMaintenanceOnHost(final PgConnection pgConnection) {
        return new StatisticsMaintenanceOnHostImpl(pgConnection);
    }
}
