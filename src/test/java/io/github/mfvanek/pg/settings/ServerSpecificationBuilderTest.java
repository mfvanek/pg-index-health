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

import io.github.mfvanek.pg.model.MemoryUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class ServerSpecificationBuilderTest {

    @Test
    void toStringTest() {
        final ServerSpecificationBuilder builder = ServerSpecification.builder();
        assertThat(builder)
                .hasToString("ServerSpecificationBuilder{cpuCoresAmount=1, memoryAmountInBytes=1073741824, hasSSD=false}");
        builder.withCpuCores(2)
                .withMemoryAmount(512, MemoryUnit.MB)
                .withSSD();
        assertThat(builder)
                .hasToString("ServerSpecificationBuilder{cpuCoresAmount=2, memoryAmountInBytes=536870912, hasSSD=true}");
    }
}
