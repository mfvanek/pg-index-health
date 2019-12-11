/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import com.mfvanek.pg.model.MemoryUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerSpecificationTest {

    @Test
    void getCpuCoresAmount() {
        final var specification = ServerSpecification.builder()
                .withCpuCores(2)
                .build();
        assertNotNull(specification);
        assertEquals(2, specification.getCpuCoresAmount());
        assertEquals(1024L * 1024L * 1024L, specification.getMemoryAmountInBytes());
        assertFalse(specification.hasSSD());

        assertThrows(IllegalArgumentException.class, () -> ServerSpecification.builder().withCpuCores(0));
        assertThrows(IllegalArgumentException.class, () -> ServerSpecification.builder().withCpuCores(-1));
    }

    @Test
    void getMemoryAmountInBytes() {
        final var specification = ServerSpecification.builder()
                .withMemoryAmount(160, MemoryUnit.GB)
                .build();
        assertNotNull(specification);
        assertEquals(1, specification.getCpuCoresAmount());
        assertEquals(160L * 1024L * 1024L * 1024L, specification.getMemoryAmountInBytes());
        assertFalse(specification.hasSSD());

        assertThrows(IllegalArgumentException.class, () -> ServerSpecification.builder().withMemoryAmount(0, MemoryUnit.GB));
        assertThrows(IllegalArgumentException.class, () -> ServerSpecification.builder().withMemoryAmount(-1, MemoryUnit.GB));
    }

    @Test
    void hasSSD() {
        final var specification = ServerSpecification.builder()
                .withSSD()
                .build();
        assertNotNull(specification);
        assertEquals(1, specification.getCpuCoresAmount());
        assertEquals(1024L * 1024L * 1024L, specification.getMemoryAmountInBytes());
        assertTrue(specification.hasSSD());
    }

    @Test
    void toStringTest() {
        final var specification = ServerSpecification.builder().build();
        assertNotNull(specification);
        assertEquals("ServerSpecification{cpuCoresAmount=1, memoryAmountInBytes=1073741824, hasSSD=false}",
                specification.toString());
    }
}
