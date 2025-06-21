/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator.utils;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameUtilsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(NameUtils.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getTableNameWithoutSchemaShouldWork() {
        final Table first = Table.of("t1", 1L);
        assertThat(NameUtils.getTableNameWithoutSchema(first))
            .isEqualTo("t1");

        final Table second = Table.of("public.t2", 1L);
        assertThat(NameUtils.getTableNameWithoutSchema(second))
            .isEqualTo("t2");

        final Table third = Table.of(".t3", 1L);
        assertThat(NameUtils.getTableNameWithoutSchema(third))
            .isEqualTo("t3");
    }
}
