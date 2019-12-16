/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.Objects;

public enum MemoryUnit {

    MB(1024L * 1024L, "megabyte"),
    GB(1024L * 1024L * 1024L, "gigabyte");

    private final long dimension;
    private final String description;

    MemoryUnit(final long dimension, @Nonnull final String description) {
        this.dimension = dimension;
        this.description = Objects.requireNonNull(description);
    }

    public long getDimension() {
        return dimension;
    }

    public long convertToBytes(final int unitsCount) {
        return unitsCount * dimension;
    }

    @Override
    public String toString() {
        return MemoryUnit.class.getSimpleName() + '{' +
                "dimension=" + dimension +
                ", description='" + description + '\'' +
                '}';
    }
}
