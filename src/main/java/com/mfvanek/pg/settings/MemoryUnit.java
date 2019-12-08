package com.mfvanek.pg.settings;

import javax.annotation.Nonnull;
import java.util.Objects;

public enum MemoryUnit {

    GB(1024L * 1024L * 1024L, "gigabyte");

    final long dimension;
    final String description;

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
}
