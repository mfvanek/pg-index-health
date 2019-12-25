/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
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
        for (var param : ImportantParam.values()) {
            assertNotNull(param.getName());
            assertNotNull(param.getDefaultValue());
        }
    }

    @Test
    void uniquenessTest() {
        final Set<String> names = new HashSet<>();
        for (var param : ImportantParam.values()) {
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
