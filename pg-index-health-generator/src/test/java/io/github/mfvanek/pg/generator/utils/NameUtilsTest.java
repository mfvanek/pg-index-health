/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator.utils;

import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.support.TestUtils;
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
