/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.function.Predicate;
import javax.sql.DataSource;

import static io.github.mfvanek.pg.spring.DatabaseStructureHealthProperties.STANDARD_DATASOURCE_BEAN_NAME;
import static org.assertj.core.api.Assertions.assertThat;

abstract class AutoConfigurationTestBase {

    protected static final List<String> EXPECTED_BEANS = List.of(
        "pgConnection",
        "duplicatedIndexesCheckOnHost",
        "foreignKeysNotCoveredWithIndexCheckOnHost",
        "indexesWithBloatCheckOnHost",
        "indexesWithNullValuesCheckOnHost",
        "intersectedIndexesCheckOnHost",
        "invalidIndexesCheckOnHost",
        "tablesWithBloatCheckOnHost",
        "tablesWithMissingIndexesCheckOnHost",
        "tablesWithoutPrimaryKeyCheckOnHost",
        "unusedIndexesCheckOnHost",
        "tablesWithoutDescriptionCheckOnHost",
        "columnsWithoutDescriptionCheckOnHost",
        "columnsWithJsonTypeCheckOnHost",
        "columnsWithSerialTypesCheckOnHost",
        "functionsWithoutDescriptionCheckOnHost",
        "indexesWithBooleanCheckOnHost",
        "notValidConstraintsCheckOnHost",
        "btreeIndexesOnArrayColumnsCheckOnHost",
        "statisticsMaintenanceOnHost",
        "sequenceOverflowCheckOnHost",
        "primaryKeysWithSerialTypesCheckOnHost",
        "duplicatedForeignKeysCheckOnHost",
        "intersectedForeignKeysCheckOnHost",
        "possibleObjectNameOverflowCheckOnHost",
        "tablesNotLinkedToOthersCheckOnHost",
        "foreignKeysWithUnmatchedColumnTypeCheckOnHost",
        "tablesWithZeroOrOneColumnCheckOnHost",
        "objectsNotFollowingNamingConventionCheckOnHost",
        "columnsNotFollowingNamingConventionCheckOnHost",
        "primaryKeysWithVarcharCheckOnHost",
        "columnsWithFixedLengthVarcharCheckOnHost",
        "indexesWithUnnecessaryWhereClauseCheckOnHost",
        "primaryKeysThatMostLikelyNaturalKeysCheckOnHost",
        "columnsWithMoneyTypeCheckOnHost"
    );
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
}
