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

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public class ServerSpecification {

    private final int cpuCoresAmount;
    private final long memoryAmountInBytes;
    private final boolean hasSSD;

    ServerSpecification(final int cpuCoresAmount,
                        final long memoryAmountInBytes,
                        final boolean hasSSD) {
        Validators.valueIsPositive(cpuCoresAmount, "cpuCoresAmount");
        this.cpuCoresAmount = cpuCoresAmount;
        this.memoryAmountInBytes = Validators.valueIsPositive(memoryAmountInBytes, "memoryAmountInBytes");
        this.hasSSD = hasSSD;
    }

    public int getCpuCoresAmount() {
        return cpuCoresAmount;
    }

    public long getMemoryAmountInBytes() {
        return memoryAmountInBytes;
    }

    public boolean hasSSD() {
        return hasSSD;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return ServerSpecification.class.getSimpleName() + '{' +
                "cpuCoresAmount=" + cpuCoresAmount +
                ", memoryAmountInBytes=" + memoryAmountInBytes +
                ", hasSSD=" + hasSSD +
                '}';
    }

    @Nonnull
    public static ServerSpecificationBuilder builder() {
        return new ServerSpecificationBuilder();
    }
}
