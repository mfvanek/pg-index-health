/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
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

public class ServerSpecification {

    private final int cpuCoresAmount;
    private final long memoryAmountInBytes;
    private final boolean hasSSD;

    private ServerSpecification(int cpuCoresAmount,
                                long memoryAmountInBytes,
                                boolean hasSSD) {
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

    @Override
    public String toString() {
        return ServerSpecification.class.getSimpleName() + '{' +
                "cpuCoresAmount=" + cpuCoresAmount +
                ", memoryAmountInBytes=" + memoryAmountInBytes +
                ", hasSSD=" + hasSSD +
                '}';
    }

    public static class Builder {

        private int cpuCoresAmount;
        private long memoryAmountInBytes;
        private boolean hasSSD;

        private Builder() {
            this.cpuCoresAmount = 1;
            this.memoryAmountInBytes = MemoryUnit.GB.convertToBytes(1); // 1 GB
            this.hasSSD = false;
        }

        public Builder withCpuCores(final int cpuCoresAmount) {
            Validators.valueIsPositive(cpuCoresAmount, "cpuCoresAmount");
            this.cpuCoresAmount = cpuCoresAmount;
            return this;
        }

        public Builder withMemoryAmount(final int unitsCount, final MemoryUnit unit) {
            Validators.valueIsPositive(unitsCount, "unitsCount");
            this.memoryAmountInBytes = unit.convertToBytes(unitsCount);
            return this;
        }

        public Builder withSSD() {
            this.hasSSD = true;
            return this;
        }

        public ServerSpecification build() {
            return new ServerSpecification(cpuCoresAmount, memoryAmountInBytes, hasSSD);
        }

        @Override
        public String toString() {
            return ServerSpecification.Builder.class.getSimpleName() + '{' +
                    "cpuCoresAmount=" + cpuCoresAmount +
                    ", memoryAmountInBytes=" + memoryAmountInBytes +
                    ", hasSSD=" + hasSSD +
                    '}';
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
