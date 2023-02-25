/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgContextTest {

    @Test
    void getSchemaNameForCustomSchema() {
        final PgContext pgContext = PgContext.of("s");
        assertThat(pgContext.getSchemaName()).isEqualTo("s");
        assertThat(pgContext.isDefaultSchema()).isFalse();
    }

    @Test
    void getSchemaNameForPublicSchema() {
        final PgContext pgContext = PgContext.ofPublic();
        assertThat(pgContext.getSchemaName()).isEqualTo(PgContext.DEFAULT_SCHEMA_NAME);
        assertThat(pgContext.isDefaultSchema()).isTrue();
    }

    @Test
    void getSchemaNameForPublicSchemaWithUpperCase() {
        final PgContext pgContext = PgContext.of("PUBLIC");
        assertThat(pgContext.getSchemaName()).isEqualTo(PgContext.DEFAULT_SCHEMA_NAME);
        assertThat(pgContext.isDefaultSchema()).isTrue();
    }

    @Test
    void getBloatPercentageThreshold() {
        assertThat(PgContext.of("s").getBloatPercentageThreshold()).isEqualTo(10);
        assertThat(PgContext.of("s", 22).getBloatPercentageThreshold()).isEqualTo(22);
        assertThat(PgContext.ofPublic().getBloatPercentageThreshold()).isEqualTo(10);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> PgContext.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("schemaName cannot be null");
        assertThatThrownBy(() -> PgContext.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("schemaName cannot be blank");
        assertThatThrownBy(() -> PgContext.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("schemaName cannot be blank");
        assertThatThrownBy(() -> PgContext.of("s", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("bloatPercentageThreshold cannot be less than zero");
    }

    @Test
    void testToString() {
        assertThat(PgContext.of("s"))
                .hasToString("PgContext{schemaName='s', bloatPercentageThreshold=10}");
        assertThat(PgContext.of("s", 11))
                .hasToString("PgContext{schemaName='s', bloatPercentageThreshold=11}");
        assertThat(PgContext.ofPublic())
                .hasToString("PgContext{schemaName='public', bloatPercentageThreshold=10}");
    }

    @Test
    void complementWithCustomSchema() {
        final PgContext pgContext = PgContext.of("TEST");
        assertThat(pgContext.enrichWithSchema("table1")).isEqualTo("test.table1");
        assertThat(pgContext.enrichWithSchema("index1")).isEqualTo("test.index1");
        assertThat(pgContext.enrichWithSchema("test.table2")).isEqualTo("test.table2");
        assertThat(pgContext.enrichWithSchema("TEST.table2")).isEqualTo("TEST.table2");
    }

    @Test
    void complementWithPublicSchema() {
        final PgContext pgContext = PgContext.ofPublic();
        assertThat(pgContext.enrichWithSchema("table1")).isEqualTo("table1");
        assertThat(pgContext.enrichWithSchema("index1")).isEqualTo("index1");
        assertThat(pgContext.enrichWithSchema("public.table2")).isEqualTo("public.table2");
        assertThat(pgContext.enrichWithSchema("PUBLIC.table2")).isEqualTo("PUBLIC.table2");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void complementWithSchemaWithInvalidArguments() {
        final PgContext pgContext = PgContext.ofPublic();
        assertThatThrownBy(() -> pgContext.enrichWithSchema(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("objectName cannot be null");
        assertThatThrownBy(() -> pgContext.enrichWithSchema(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("objectName cannot be blank");
        assertThatThrownBy(() -> pgContext.enrichWithSchema("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("objectName cannot be blank");
    }
}
