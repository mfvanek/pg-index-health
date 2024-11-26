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
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.settings.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

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
        "configurationMaintenanceOnHost",
        "sequenceOverflowCheckOnHost",
        "primaryKeysWithSerialTypesCheckOnHost",
        "duplicatedForeignKeysCheckOnHost",
        "intersectedForeignKeysCheckOnHost",
        "possibleObjectNameOverflowCheckOnHost",
        "tablesNotLinkedToOthersCheckOnHost",
        "foreignKeysWithUnmatchedColumnTypeCheckOnHost"
    );
    protected static final Class<?>[] EXPECTED_TYPES = {PgConnection.class, DatabaseCheckOnHost.class, StatisticsMaintenanceOnHost.class, ConfigurationMaintenanceOnHost.class};
    protected static final DataSource DATA_SOURCE_MOCK = Mockito.mock(DataSource.class);

    protected final Predicate<String> beanNamesFilter = b -> !b.startsWith("org.springframework") && !b.startsWith("pg.index.health.test") &&
        !b.endsWith("AutoConfiguration") && !"dataSource".equals(b);
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Nonnull
    protected ApplicationContextRunner assertWithTestConfig() {
        return contextRunner.withUserConfiguration(DatabaseStructureHealthAutoConfiguration.class, DatabaseStructureChecksAutoConfiguration.class);
    }

    protected static <C extends ConfigurableApplicationContext> void initialize(@Nonnull final C applicationContext) {
        final GenericApplicationContext context = (GenericApplicationContext) applicationContext;
        context.registerBean("dataSource", DataSource.class, () -> DATA_SOURCE_MOCK);
    }

    @Nonnull
    protected static String getBeanName(@Nonnull final Class<?> type) {
        return StringUtils.uncapitalize(type.getSimpleName());
    }

    protected void assertThatBeansAreNotNullBean(@Nonnull final ConfigurableApplicationContext context) {
        EXPECTED_BEANS.forEach(beanName ->
            assertThatBeanIsNotNullBean(context, beanName));
    }

    protected void assertThatBeanIsNotNullBean(@Nonnull final ConfigurableApplicationContext context, @Nonnull final String beanName) {
        assertThat(context.getBean(beanName))
            .isInstanceOfAny(EXPECTED_TYPES);
    }
}
