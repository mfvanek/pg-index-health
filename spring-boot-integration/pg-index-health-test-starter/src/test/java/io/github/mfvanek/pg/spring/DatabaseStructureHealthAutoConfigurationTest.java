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
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseStructureHealthAutoConfigurationTest extends AutoConfigurationTestBase {

    @Test
    void propertiesDefaultValueShouldBeUsed() {
        assertWithTestConfig()
            .run(context -> assertThat(context.getBean(DatabaseStructureHealthProperties.class))
                .isInstanceOf(DatabaseStructureHealthProperties.class)
                .satisfies(p -> assertThat(p.isEnabled()).isTrue()));
    }

    @Test
    void withoutDataSource() {
        assertWithTestConfig()
            .run(context -> assertThat(context.getBeanDefinitionNames())
                .isNotEmpty()
                .filteredOn(beanNamesFilter)
                .isEmpty());
    }

    @Test
    void withDataSource() {
        assertWithTestConfig()
            .withPropertyValues("spring.datasource.url=jdbc:postgresql://localhost:5432")
            .withInitializer(AutoConfigurationTestBase::initialize)
            .run(context -> {
                assertThatBeansPresent(context);
                assertThatBeansAreNotNullBean(context);
            });
    }

    @Test
    void withCustomDataSource() {
        assertWithTestConfig()
            .withPropertyValues("custom.datasource.url=jdbc:postgresql://localhost:5432",
                "pg.index.health.test.datasource-bean-name=customDataSource",
                "pg.index.health.test.datasource-url-property-name=custom.datasource.url")
            .withInitializer(AutoConfigurationTestBase::initializeCustom)
            .run(context -> {
                assertThatBeansPresent(context);
                assertThatBeansAreNotNullBean(context);
            });
    }

    @Test
    void withDataSourceButWithoutConnectionString() throws SQLException {
        try (Connection connectionMock = Mockito.mock(Connection.class)) {
            setMocks(connectionMock);

            assertWithTestConfig()
                .withInitializer(AutoConfigurationTestBase::initialize)
                .run(context -> {
                    assertThatBeansPresent(context);
                    assertThatBeansAreNotNullBean(context);
                    assertThatPgConnectionIsValid(context);
                });
        }
    }

    @Test
    void withCustomDataSourceButWithoutConnectionString() throws SQLException {
        try (Connection connectionMock = Mockito.mock(Connection.class)) {
            setMocks(connectionMock);

            assertWithTestConfig()
                .withInitializer(AutoConfigurationTestBase::initializeCustom)
                .withPropertyValues("pg.index.health.test.datasource-bean-name=customDataSource",
                    "pg.index.health.test.datasource-url-property-name=custom.datasource.url")
                .run(context -> {
                    assertThatBeansPresent(context);
                    assertThatBeansAreNotNullBean(context);
                    assertThatPgConnectionIsValid(context);
                });
        }
    }

    @Test
    void withDataSourceAndEmptyConnectionString() throws SQLException {
        try (Connection connectionMock = Mockito.mock(Connection.class)) {
            setMocks(connectionMock);

            assertWithTestConfig()
                .withPropertyValues("spring.datasource.url=")
                .withInitializer(AutoConfigurationTestBase::initialize)
                .run(context -> {
                    assertThatBeansPresent(context);
                    assertThatBeansAreNotNullBean(context);
                    assertThatPgConnectionIsValid(context);
                });
        }
    }

    @Test
    void withCustomDataSourceAndEmptyConnectionString() throws SQLException {
        try (Connection connectionMock = Mockito.mock(Connection.class)) {
            setMocks(connectionMock);

            assertWithTestConfig()
                .withPropertyValues("custom.datasource.url=",
                    "pg.index.health.test.datasource-bean-name=customDataSource",
                    "pg.index.health.test.datasource-url-property-name=custom.datasource.url")
                .withInitializer(AutoConfigurationTestBase::initializeCustom)
                .run(context -> {
                    assertThatBeansPresent(context);
                    assertThatBeansAreNotNullBean(context);
                    assertThatPgConnectionIsValid(context);
                });
        }
    }

    @Test
    void withDataSourceAndWrongConnectionString() {
        assertWithTestConfig()
            .withPropertyValues("spring.datasource.url=jdbc:mysql://localhost/test")
            .withInitializer(AutoConfigurationTestBase::initialize)
            .run(context -> {
                assertThat(context.getBeansOfType(DatabaseStructureHealthProperties.class))
                    .isEmpty();
                assertThat(context.getBeanDefinitionNames())
                    .isNotEmpty()
                    .filteredOn(beanNamesFilter)
                    .isEmpty();
            });
    }

    @Test
    void withCustomDataSourceAndWrongConnectionString() {
        assertWithTestConfig()
            .withPropertyValues("custom.datasource.url=jdbc:mysql://localhost/test",
                "pg.index.health.test.datasource-bean-name=customDataSource",
                "pg.index.health.test.datasource-url-property-name=custom.datasource.url")
            .withInitializer(AutoConfigurationTestBase::initializeCustom)
            .run(context -> {
                assertThat(context.getBeansOfType(DatabaseStructureHealthProperties.class))
                    .isEmpty();
                assertThat(context.getBeanDefinitionNames())
                    .isNotEmpty()
                    .filteredOn(beanNamesFilter)
                    .isEmpty();
            });
    }

    @Test
    void withDataSourceAndTestcontainersConnectionString() throws SQLException {
        try (Connection connectionMock = Mockito.mock(Connection.class)) {
            setMocks(connectionMock);

            assertWithTestConfig()
                .withPropertyValues("spring.datasource.url=jdbc:tc:postgresql:17.4:///test")
                .withInitializer(AutoConfigurationTestBase::initialize)
                .run(context -> {
                    assertThatBeansPresent(context);
                    assertThatBeansAreNotNullBean(context);
                    assertThatPgConnectionIsValid(context);
                });
        }
    }

    @Test
    void shouldNotCreateAutoConfigurationWithDisabledProperty() {
        assertWithTestConfig()
            .withPropertyValues("pg.index.health.test.enabled=false")
            .withInitializer(AutoConfigurationTestBase::initialize)
            .run(context -> {
                assertThat(context.getBeansOfType(DatabaseStructureHealthProperties.class))
                    .isEmpty();
                assertThat(context.getBeanDefinitionNames())
                    .isNotEmpty()
                    .filteredOn(beanNamesFilter)
                    .isEmpty();
            });
    }

    @Test
    void shouldCreateAutoConfigurationWhenPropertyExplicitlySet() {
        assertWithTestConfig()
            .withPropertyValues("pg.index.health.test.enabled=true",
                "spring.datasource.url=jdbc:postgresql://localhost:5432")
            .withInitializer(AutoConfigurationTestBase::initialize)
            .run(context -> {
                assertThatBeansPresent(context);
                assertThatBeansAreNotNullBean(context);
            });
    }

    @Test
    void withDataSourceAndExceptionWhileObtainingUrlFromMetadata() throws SQLException {
        try (Connection connectionMock = Mockito.mock(Connection.class)) {
            Mockito.when(DATA_SOURCE_MOCK.getConnection())
                .thenReturn(connectionMock);
            Mockito.when(connectionMock.getMetaData())
                .thenThrow(SQLException.class);

            final ApplicationContextRunner contextRunner = assertWithTestConfig()
                .withInitializer(AutoConfigurationTestBase::initialize);
            assertThatThrownBy(() -> contextRunner.run(this::assertThatPgConnectionIsValid))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("""
                    Unstarted application context org.springframework.boot.test.context.assertj.AssertableApplicationContext[\
                    startupFailure=org.springframework.beans.factory.BeanCreationException] failed to start""")
                .hasStackTraceContaining("Factory method 'pgConnection' threw exception with message: null");
        }
    }

    private void assertThatBeansPresent(@NonNull final ConfigurableApplicationContext context) {
        assertThat(context.getBeanDefinitionNames())
            .isNotEmpty()
            .filteredOn(beanNamesFilter)
            .hasSameSizeAs(EXPECTED_BEANS)
            .containsAll(EXPECTED_BEANS);
    }

    private void assertThatPgConnectionIsValid(@NonNull final ConfigurableApplicationContext context) {
        assertThat(context.getBean("pgConnection", PgConnection.class))
            .isNotNull()
            .satisfies(c -> assertThat(c.getHost())
                .isEqualTo(PgHostImpl.ofUrl("jdbc:postgresql://192.168.1.1:6432")));
    }

    private void setMocks(@NonNull final Connection connectionMock) throws SQLException {
        Mockito.when(DATA_SOURCE_MOCK.getConnection())
            .thenReturn(connectionMock);
        final DatabaseMetaData metaDataMock = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(connectionMock.getMetaData())
            .thenReturn(metaDataMock);
        Mockito.when(metaDataMock.getURL())
            .thenReturn("jdbc:postgresql://192.168.1.1:6432");
    }
}
