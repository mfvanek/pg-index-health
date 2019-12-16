/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImportantParamTest {

    @Test
    void completenessTest() {
        for (var param : ImportantParam.values()) {
            assertNotNull(param.getName());
            assertNotNull(param.getValue());
            assertNotNull(param.getDefaultValue());
        }
    }

    @Test
    void testToString() {
        final String result = ImportantParam.MAINTENANCE_WORK_MEM.toString();
        assertEquals("ImportantParam{defaultValue='PgParamImpl{name='maintenance_work_mem', value='64MB'}'}", result);
    }
}
