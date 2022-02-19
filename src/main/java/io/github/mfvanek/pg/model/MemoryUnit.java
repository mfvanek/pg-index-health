/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Units of information.
 *
 * @author Ivan Vakhrushev
 */
public enum MemoryUnit {

    KB(1024L, "kilobyte"),
    MB(1024L * 1024L, "megabyte"),
    GB(1024L * 1024L * 1024L, "gigabyte");

    private final long dimension;
    private final String description;

    MemoryUnit(final long dimension, @Nonnull final String description) {
        this.dimension = dimension;
        this.description = Objects.requireNonNull(description);
    }

    /**
     * Converts given information units amount to bytes according to it {@link MemoryUnit#dimension}.
     *
     * @param unitsCount information units amount
     * @return size in bytes
     */
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
