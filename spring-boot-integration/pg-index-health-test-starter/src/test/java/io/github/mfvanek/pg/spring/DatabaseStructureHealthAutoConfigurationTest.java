/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHostImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.annotation.Nonnull;

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
                .hasMessage("Unstarted application context org.springframework.boot.test.context.assertj.AssertableApplicationContext[" +
                    "startupFailure=org.springframework.beans.factory.BeanCreationException] failed to start")
                .hasStackTraceContaining("Factory method 'pgConnection' threw exception; nested exception is io.github.mfvanek.pg.connection.PgSqlException");
        }
    }

    private void assertThatBeansPresent(@Nonnull final ConfigurableApplicationContext context) {
        assertThat(context.getBeanDefinitionNames())
            .isNotEmpty()
            .filteredOn(beanNamesFilter)
            .hasSameSizeAs(EXPECTED_BEANS)
            .containsAll(EXPECTED_BEANS);
    }

    private void assertThatPgConnectionIsValid(@Nonnull final ConfigurableApplicationContext context) {
        assertThat(context.getBean("pgConnection", PgConnection.class))
            .isNotNull()
            .satisfies(c -> assertThat(c.getHost())
                .isEqualTo(PgHostImpl.ofUrl("jdbc:postgresql://192.168.1.1:6432")));
    }

    private void setMocks(@Nonnull final Connection connectionMock) throws SQLException {
        Mockito.when(DATA_SOURCE_MOCK.getConnection())
            .thenReturn(connectionMock);
        final DatabaseMetaData metaDataMock = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(connectionMock.getMetaData())
            .thenReturn(metaDataMock);
        Mockito.when(metaDataMock.getURL())
            .thenReturn("jdbc:postgresql://192.168.1.1:6432");
    }
}
