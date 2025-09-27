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
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.BtreeIndexesOnArrayColumnsCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsNotFollowingNamingConventionCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithFixedLengthVarcharCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithJsonTypeCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithMoneyTypeCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.ColumnsWithTimestampOrTimetzTypeCheckOnHost;
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
import io.github.mfvanek.pg.core.checks.host.TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWherePrimaryKeyColumnsNotFirstCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithBloatCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithMissingIndexesCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithZeroOrOneColumnCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.TablesWithoutPrimaryKeyCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.sql.DataSource;

import static io.github.mfvanek.pg.spring.DatabaseStructureHealthProperties.STANDARD_DATASOURCE_BEAN_NAME;
import static org.assertj.core.api.Assertions.assertThat;

abstract class AutoConfigurationTestBase {

    protected static final List<String> EXPECTED_BEANS = Stream.concat(
            Stream.of("pgConnection"),
            getCheckTypes().stream()
                .map(Class::getSimpleName)
                .map(s -> s.substring(0, 1).toLowerCase(Locale.ROOT) + s.substring(1)))
        .toList();

    protected static final Class<?>[] EXPECTED_TYPES = {PgConnection.class, DatabaseCheckOnHost.class, StatisticsMaintenanceOnHost.class};
    protected static final DataSource DATA_SOURCE_MOCK = Mockito.mock(DataSource.class);

    private static final String CUSTOM_DATASOURCE_BEAN_NAME = "customDataSource";

    protected final Predicate<String> beanNamesFilter = b -> !b.startsWith("org.springframework") &&
        !b.startsWith("pg.index.health.test") &&
        !b.endsWith("AutoConfiguration") &&
        !STANDARD_DATASOURCE_BEAN_NAME.equals(b) &&
        !CUSTOM_DATASOURCE_BEAN_NAME.equals(b);
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @NonNull
    protected ApplicationContextRunner assertWithTestConfig() {
        return contextRunner.withUserConfiguration(DatabaseStructureHealthAutoConfiguration.class, DatabaseStructureChecksAutoConfiguration.class);
    }

    protected static <C extends ConfigurableApplicationContext> void initialize(@NonNull final C applicationContext) {
        final GenericApplicationContext context = (GenericApplicationContext) applicationContext;
        context.registerBean(STANDARD_DATASOURCE_BEAN_NAME, DataSource.class, () -> DATA_SOURCE_MOCK);
    }

    protected static <C extends ConfigurableApplicationContext> void initializeCustom(@NonNull final C applicationContext) {
        final GenericApplicationContext context = (GenericApplicationContext) applicationContext;
        context.registerBean(CUSTOM_DATASOURCE_BEAN_NAME, DataSource.class, () -> DATA_SOURCE_MOCK);
    }

    @NonNull
    protected static String getBeanName(@NonNull final Class<?> type) {
        return StringUtils.uncapitalize(type.getSimpleName());
    }

    protected void assertThatBeansAreNotNullBean(@NonNull final ConfigurableApplicationContext context) {
        EXPECTED_BEANS.forEach(beanName ->
            assertThatBeanIsNotNullBean(context, beanName));
    }

    protected void assertThatBeanIsNotNullBean(@NonNull final ConfigurableApplicationContext context, @NonNull final String beanName) {
        assertThat(context.getBean(beanName))
            .isInstanceOfAny(EXPECTED_TYPES);
    }

    protected static List<Class<?>> getCheckTypes() {
        return List.of(
            DuplicatedIndexesCheckOnHost.class,
            ForeignKeysNotCoveredWithIndexCheckOnHost.class,
            IndexesWithBloatCheckOnHost.class,
            IndexesWithNullValuesCheckOnHost.class,
            IntersectedIndexesCheckOnHost.class,
            InvalidIndexesCheckOnHost.class,
            TablesWithBloatCheckOnHost.class,
            TablesWithMissingIndexesCheckOnHost.class,
            TablesWithoutPrimaryKeyCheckOnHost.class,
            UnusedIndexesCheckOnHost.class,
            TablesWithoutDescriptionCheckOnHost.class,
            ColumnsWithoutDescriptionCheckOnHost.class,
            ColumnsWithJsonTypeCheckOnHost.class,
            ColumnsWithSerialTypesCheckOnHost.class,
            FunctionsWithoutDescriptionCheckOnHost.class,
            IndexesWithBooleanCheckOnHost.class,
            NotValidConstraintsCheckOnHost.class,
            BtreeIndexesOnArrayColumnsCheckOnHost.class,
            StatisticsMaintenanceOnHost.class,
            SequenceOverflowCheckOnHost.class,
            PrimaryKeysWithSerialTypesCheckOnHost.class,
            DuplicatedForeignKeysCheckOnHost.class,
            IntersectedForeignKeysCheckOnHost.class,
            PossibleObjectNameOverflowCheckOnHost.class,
            TablesNotLinkedToOthersCheckOnHost.class,
            ForeignKeysWithUnmatchedColumnTypeCheckOnHost.class,
            TablesWithZeroOrOneColumnCheckOnHost.class,
            ObjectsNotFollowingNamingConventionCheckOnHost.class,
            ColumnsNotFollowingNamingConventionCheckOnHost.class,
            PrimaryKeysWithVarcharCheckOnHost.class,
            ColumnsWithFixedLengthVarcharCheckOnHost.class,
            IndexesWithUnnecessaryWhereClauseCheckOnHost.class,
            PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost.class,
            ColumnsWithMoneyTypeCheckOnHost.class,
            IndexesWithTimestampInTheMiddleCheckOnHost.class,
            ColumnsWithTimestampOrTimetzTypeCheckOnHost.class,
            TablesWherePrimaryKeyColumnsNotFirstCheckOnHost.class,
            TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnHost.class
        );
    }
}
