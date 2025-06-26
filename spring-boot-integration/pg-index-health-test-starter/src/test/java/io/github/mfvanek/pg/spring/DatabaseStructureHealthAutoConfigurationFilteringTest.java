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

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.FilteredClassLoader;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseStructureHealthAutoConfigurationFilteringTest extends AutoConfigurationTestBase {

    private static final int ADDITIONAL_BEANS = 2; // pgConnection + statisticsMaintenanceOnHost

    @Test
    void beansCompleteness() {
        assertThat(EXPECTED_BEANS)
            .as("All checks should be added to the starter")
            .hasSize(Diagnostic.values().length + ADDITIONAL_BEANS);

        assertThat(getCheckTypes())
            .hasSize(Diagnostic.values().length + 1); // + statisticsMaintenanceOnHost
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
}
