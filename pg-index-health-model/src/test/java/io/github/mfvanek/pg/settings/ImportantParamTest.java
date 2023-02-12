/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.settings;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ImportantParamTest {

    @Test
    void completenessTest() {
        for (final ImportantParam param : ImportantParam.values()) {
            assertThat(param.getName()).isNotNull();
            assertThat(param.getDefaultValue()).isNotNull();
        }
    }

    @Test
    void uniquenessTest() {
        final Set<String> names = new HashSet<>();
        for (final ImportantParam param : ImportantParam.values()) {
            names.add(param.getName());
        }
        assertThat(names).hasSize(ImportantParam.values().length);
    }

    @Test
    void testToString() {
        assertThat(ImportantParam.MAINTENANCE_WORK_MEM)
                .hasToString("ImportantParam{name='maintenance_work_mem', defaultValue='64MB'}");
    }
}
