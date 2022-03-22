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

import io.github.mfvanek.pg.model.MemoryUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServerSpecificationTest {

    @Test
    void getCpuCoresAmount() {
        final ServerSpecification specification = ServerSpecification.builder()
                .withCpuCores(2)
                .build();
        assertThat(specification).isNotNull();
        assertThat(specification.getCpuCoresAmount()).isEqualTo(2);
        assertThat(specification.getMemoryAmountInBytes()).isEqualTo(1024L * 1024L * 1024L);
        assertThat(specification.hasSSD()).isFalse();

        assertThatThrownBy(() -> ServerSpecification.builder().withCpuCores(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ServerSpecification.builder().withCpuCores(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getMemoryAmountInBytes() {
        final ServerSpecification specification = ServerSpecification.builder()
                .withMemoryAmount(160, MemoryUnit.GB)
                .build();
        assertThat(specification).isNotNull();
        assertThat(specification.getCpuCoresAmount()).isEqualTo(1);
        assertThat(specification.getMemoryAmountInBytes()).isEqualTo(160L * 1024L * 1024L * 1024L);
        assertThat(specification.hasSSD()).isFalse();

        assertThatThrownBy(() -> ServerSpecification.builder().withMemoryAmount(0, MemoryUnit.GB)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ServerSpecification.builder().withMemoryAmount(-1, MemoryUnit.GB)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void hasSSD() {
        final ServerSpecification specification = ServerSpecification.builder()
                .withSSD()
                .build();
        assertThat(specification).isNotNull();
        assertThat(specification.getCpuCoresAmount()).isEqualTo(1);
        assertThat(specification.getMemoryAmountInBytes()).isEqualTo(1024L * 1024L * 1024L);
        assertThat(specification.hasSSD()).isTrue();
    }

    @Test
    void toStringTest() {
        final ServerSpecification specification = ServerSpecification.builder().build();
        assertThat(specification).isNotNull();
        assertThat(specification.toString()).isEqualTo("ServerSpecification{cpuCoresAmount=1, memoryAmountInBytes=1073741824, hasSSD=false}");
    }
}
