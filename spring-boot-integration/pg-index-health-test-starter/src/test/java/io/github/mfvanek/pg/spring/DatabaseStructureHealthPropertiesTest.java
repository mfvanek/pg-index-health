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

import org.junit.jupiter.api.Test;

import static io.github.mfvanek.pg.spring.DatabaseStructureHealthProperties.STANDARD_DATASOURCE_BEAN_NAME;
import static io.github.mfvanek.pg.spring.DatabaseStructureHealthProperties.STANDARD_DATASOURCE_URL_PROPERTY_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseStructureHealthPropertiesTest {

    @Test
    void getterShouldWorkWhenEnabled() {
        final DatabaseStructureHealthProperties propertiesEnabled =
            new DatabaseStructureHealthProperties(true, STANDARD_DATASOURCE_BEAN_NAME, STANDARD_DATASOURCE_URL_PROPERTY_NAME);
        assertThat(propertiesEnabled.isEnabled())
            .isTrue();
        assertThat(propertiesEnabled.getDatasourceBeanName())
            .isEqualTo(STANDARD_DATASOURCE_BEAN_NAME);
        assertThat(propertiesEnabled.getDatasourceUrlPropertyName())
            .isEqualTo(STANDARD_DATASOURCE_URL_PROPERTY_NAME);
        assertThat(propertiesEnabled)
            .hasToString("DatabaseStructureHealthProperties{enabled=true, datasourceBeanName='dataSource', datasourceUrlPropertyName='spring.datasource.url'}");
    }

    @Test
    void getterShouldWorkWhenDisabled() {
        final DatabaseStructureHealthProperties propertiesDisabled =
            new DatabaseStructureHealthProperties(false, "customDataSource", "custom.datasource.url");
        assertThat(propertiesDisabled.isEnabled())
            .isFalse();
        assertThat(propertiesDisabled.getDatasourceBeanName())
            .isEqualTo("customDataSource");
        assertThat(propertiesDisabled.getDatasourceUrlPropertyName())
            .isEqualTo("custom.datasource.url");
        assertThat(propertiesDisabled)
            .hasToString("DatabaseStructureHealthProperties{enabled=false, datasourceBeanName='customDataSource', datasourceUrlPropertyName='custom.datasource.url'}");
    }

    @Test
    void shouldThrowExceptionWhenInvalidArgumentsArePassed() {
        assertThatThrownBy(() -> new DatabaseStructureHealthProperties(true, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("datasourceBeanName cannot be null");

        assertThatThrownBy(() -> new DatabaseStructureHealthProperties(true, "", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("datasourceBeanName cannot be blank");

        assertThatThrownBy(() -> new DatabaseStructureHealthProperties(true, "   ", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("datasourceBeanName cannot be blank");

        assertThatThrownBy(() -> new DatabaseStructureHealthProperties(true, "beanName", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("datasourceUrlPropertyName cannot be null");

        assertThatThrownBy(() -> new DatabaseStructureHealthProperties(true, "beanName", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("datasourceUrlPropertyName cannot be blank");

        assertThatThrownBy(() -> new DatabaseStructureHealthProperties(true, "beanName", "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("datasourceUrlPropertyName cannot be blank");
    }
}
