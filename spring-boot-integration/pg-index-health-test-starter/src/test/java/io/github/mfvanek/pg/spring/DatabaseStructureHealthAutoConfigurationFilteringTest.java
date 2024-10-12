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
import io.github.mfvanek.pg.checks.host.FunctionsWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithBloatCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithBooleanCheckOnHost;
import io.github.mfvanek.pg.checks.host.IndexesWithNullValuesCheckOnHost;
import io.github.mfvanek.pg.checks.host.IntersectedForeignKeysCheckOnHost;
import io.github.mfvanek.pg.checks.host.IntersectedIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.InvalidIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.NotValidConstraintsCheckOnHost;
import io.github.mfvanek.pg.checks.host.PrimaryKeysWithSerialTypesCheckOnHost;
import io.github.mfvanek.pg.checks.host.SequenceOverflowCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithBloatCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithMissingIndexesCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithoutDescriptionCheckOnHost;
import io.github.mfvanek.pg.checks.host.TablesWithoutPrimaryKeyCheckOnHost;
import io.github.mfvanek.pg.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.FilteredClassLoader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseStructureHealthAutoConfigurationFilteringTest extends AutoConfigurationTestBase {

    private static final int ADDITIONAL_BEANS = 3; // pgConnection + statisticsMaintenanceOnHost + configurationMaintenanceOnHost

    @Test
    void beansCompleteness() {
        assertThat(EXPECTED_BEANS)
            .as("All checks should be added to the starter")
            .hasSize(Diagnostic.values().length + ADDITIONAL_BEANS);

        assertThat(getCheckTypes())
            .hasSize(Diagnostic.values().length + 2); // statisticsMaintenanceOnHost + configurationMaintenanceOnHost
    }

    @ParameterizedTest
    @MethodSource("getCheckTypes")
    void withoutClass(final Class<?> type) {
        assertWithTestConfig()
            .withPropertyValues("spring.datasource.url=jdbc:postgresql://localhost:5432")
            .withInitializer(AutoConfigurationTestBase::initialize)
            .withClassLoader(new FilteredClassLoader(type))
            .run(context -> assertThat(context)
                .hasBean("pgConnection")
                .doesNotHaveBean(getBeanName(type))
                .satisfies(c -> assertThat(c.getBeanDefinitionNames())
                    .isNotEmpty()
                    .filteredOn(beanNamesFilter)
                    .hasSize(EXPECTED_BEANS.size() - 1)
                    .allSatisfy(beanName ->
                        assertThatBeanIsNotNullBean(context, beanName)))
            );
    }

    @Test
    void withDataSourceAndEmptyConnectionStringAndWithoutDriver() {
        assertWithTestConfig()
            .withPropertyValues("spring.datasource.url=")
            .withInitializer(AutoConfigurationTestBase::initialize)
            .withClassLoader(new FilteredClassLoader(org.postgresql.Driver.class))
            .run(context -> {
                assertThat(context.getBeansOfType(DatabaseStructureHealthProperties.class))
                    .isEmpty();
                assertThat(context.getBeanDefinitionNames())
                    .isNotEmpty()
                    .filteredOn(beanNamesFilter)
                    .isEmpty();
            });
    }

    private static List<Class<?>> getCheckTypes() {
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
            ConfigurationMaintenanceOnHost.class,
            SequenceOverflowCheckOnHost.class,
            PrimaryKeysWithSerialTypesCheckOnHost.class,
            DuplicatedForeignKeysCheckOnHost.class,
            IntersectedForeignKeysCheckOnHost.class
        );
    }
}
