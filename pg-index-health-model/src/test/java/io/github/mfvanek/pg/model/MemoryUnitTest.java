/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
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

class MemoryUnitTest {

    @Test
    void convertToBytes() {
        assertThat(MemoryUnit.KB.convertToBytes(1)).isEqualTo(1_024L);
        assertThat(MemoryUnit.MB.convertToBytes(2)).isEqualTo(2_097_152L);
        assertThat(MemoryUnit.GB.convertToBytes(3)).isEqualTo(3_221_225_472L);
    }

    @Test
    void toStringTest() {
        assertThat(MemoryUnit.MB)
            .hasToString("MemoryUnit{dimension=1048576, description='megabyte'}");
    }
}
