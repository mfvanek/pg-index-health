/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.context;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgContextTest {

    @Test
    void getSchemaNameForCustomSchema() {
        final PgContext ctx = PgContext.of("s");
        assertThat(ctx.getSchemaName()).isEqualTo("s");
        assertThat(ctx.isDefaultSchema()).isFalse();
    }

    @Test
    void getSchemaNameForPublicSchema() {
        final PgContext ctx = PgContext.ofDefault();
        assertThat(ctx.getSchemaName()).isEqualTo(PgContext.DEFAULT_SCHEMA_NAME);
        assertThat(ctx.isDefaultSchema()).isTrue();
        assertThat(PgContext.ofDefault())
            .isSameAs(ctx)
            .isEqualTo(PgContext.of(PgContext.DEFAULT_SCHEMA_NAME));
    }

    @Test
    void getSchemaNameForPublicSchemaWithUpperCase() {
        final PgContext ctx = PgContext.of("PUBLIC");
        assertThat(ctx.getSchemaName()).isEqualTo(PgContext.DEFAULT_SCHEMA_NAME);
        assertThat(ctx.isDefaultSchema()).isTrue();
    }

    @Test
    void getBloatPercentageThreshold() {
        assertThat(PgContext.of("s").getBloatPercentageThreshold())
            .isEqualTo(10.0);
        assertThat(PgContext.of("s", 22.0).getBloatPercentageThreshold())
            .isEqualTo(22.0);
        assertThat(PgContext.ofDefault().getBloatPercentageThreshold())
            .isEqualTo(10.0);
        assertThat(PgContext.of("s").getRemainingPercentageThreshold())
            .isEqualTo(10.0);
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
            .hasMessage("bloatPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> PgContext.of("s", 1, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("remainingPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
    }

    @Test
    void testToString() {
        assertThat(PgContext.of("s"))
            .hasToString("PgContext{schemaName='s', bloatPercentageThreshold=10.0, remainingPercentageThreshold=10.0}");
        assertThat(PgContext.of("s", 11))
            .hasToString("PgContext{schemaName='s', bloatPercentageThreshold=11.0, remainingPercentageThreshold=10.0}");
        assertThat(PgContext.ofDefault())
            .hasToString("PgContext{schemaName='public', bloatPercentageThreshold=10.0, remainingPercentageThreshold=10.0}");
        assertThat(PgContext.of("s", 11, 15.0))
            .hasToString("PgContext{schemaName='s', bloatPercentageThreshold=11.0, remainingPercentageThreshold=15.0}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final PgContext first = PgContext.of("s", 99.0, 99.0);
        final PgContext theSame = PgContext.of("s", 99.0, 99.0);
        final PgContext second = PgContext.of("d", 99.0, 99.0);
        final PgContext third = PgContext.of("s", 10.0, 99.0);
        final PgContext forth = PgContext.of("s", 99.0, 10.0);

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        // self
        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // others
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        assertThat(forth)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(PgContext.class)
            .verify();
    }

    @Test
    void complementWithCustomSchema() {
        final PgContext ctx = PgContext.of("TEST");
        assertThat(ctx.enrichWithSchema("table1"))
            .isEqualTo("test.table1");
        assertThat(ctx.enrichWithSchema("index1"))
            .isEqualTo("test.index1");
        assertThat(ctx.enrichWithSchema("test.table2"))
            .isEqualTo("test.table2");
        assertThat(ctx.enrichWithSchema("TEST.table2"))
            .isEqualTo("TEST.table2");
        assertThat(PgContext.enrichWith("table1", ctx))
            .isEqualTo("test.table1");
    }

    @Test
    void complementWithPublicSchema() {
        final PgContext ctx = PgContext.ofDefault();
        assertThat(ctx.enrichWithSchema("table1"))
            .isEqualTo("table1");
        assertThat(ctx.enrichWithSchema("index1"))
            .isEqualTo("index1");
        assertThat(ctx.enrichWithSchema("public.table2"))
            .isEqualTo("public.table2");
        assertThat(ctx.enrichWithSchema("PUBLIC.table2"))
            .isEqualTo("PUBLIC.table2");
        assertThat(PgContext.enrichWith("table1", ctx))
            .isEqualTo("table1");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void complementWithSchemaWithInvalidArguments() {
        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> ctx.enrichWithSchema(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("objectName cannot be null");
        assertThatThrownBy(() -> ctx.enrichWithSchema(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("objectName cannot be blank");
        assertThatThrownBy(() -> ctx.enrichWithSchema("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("objectName cannot be blank");
        assertThatThrownBy(() -> PgContext.enrichWith(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("ctx cannot be null");
    }
}
