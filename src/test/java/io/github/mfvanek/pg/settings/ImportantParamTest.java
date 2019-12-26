/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.settings;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

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
        assertEquals(ImportantParam.values().length, names.size());
    }

    @Test
    void testToString() {
        final String result = ImportantParam.MAINTENANCE_WORK_MEM.toString();
        assertEquals("ImportantParam{name='maintenance_work_mem', defaultValue='64MB'}", result);
    }
}
