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
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.FilteredClassLoader;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseStructureHealthAutoConfigurationFilteringTest extends AutoConfigurationTestBase {

    @ParameterizedTest
    @ValueSource(classes = {
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
        ConfigurationMaintenanceOnHost.class})
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
}
