/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImportantParamTest {

    @Test
    void completenessTest() {
        for (ImportantParam param : ImportantParam.values()) {
            assertNotNull(param.getName());
            assertNotNull(param.getDefaultValue());
        }
    }

    @Test
    void uniquenessTest() {
        final Set<String> names = new HashSet<>();
        for (ImportantParam param : ImportantParam.values()) {
            names.add(param.getName());
        }
        assertThat(names, hasSize(ImportantParam.values().length));
    }

    @Test
    void testToString() {
        final String result = ImportantParam.MAINTENANCE_WORK_MEM.toString();
        assertEquals("ImportantParam{name='maintenance_work_mem', defaultValue='64MB'}", result);
    }
}
