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
import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public class ServerSpecificationBuilder {

    private int cpuCoresAmount;
    private long memoryAmountInBytes;
    private boolean hasSSD;

    ServerSpecificationBuilder() {
        this.cpuCoresAmount = 1;
        this.memoryAmountInBytes = MemoryUnit.GB.convertToBytes(1); // 1 GB
        this.hasSSD = false;
    }

    @Nonnull
    public ServerSpecificationBuilder withCpuCores(final int cpuCoresAmount) {
        Validators.valueIsPositive(cpuCoresAmount, "cpuCoresAmount");
        this.cpuCoresAmount = cpuCoresAmount;
        return this;
    }

    @Nonnull
    public ServerSpecificationBuilder withMemoryAmount(final int unitsCount, final MemoryUnit unit) {
        Validators.valueIsPositive(unitsCount, "unitsCount");
        this.memoryAmountInBytes = unit.convertToBytes(unitsCount);
        return this;
    }

    @Nonnull
    public ServerSpecificationBuilder withSSD() {
        this.hasSSD = true;
        return this;
    }

    @Nonnull
    public ServerSpecification build() {
        return new ServerSpecification(cpuCoresAmount, memoryAmountInBytes, hasSSD);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return ServerSpecificationBuilder.class.getSimpleName() + '{' +
                "cpuCoresAmount=" + cpuCoresAmount +
                ", memoryAmountInBytes=" + memoryAmountInBytes +
                ", hasSSD=" + hasSSD +
                '}';
    }
}
