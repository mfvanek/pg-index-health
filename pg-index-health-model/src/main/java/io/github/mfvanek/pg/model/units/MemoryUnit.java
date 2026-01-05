/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.units;

import java.util.Objects;

/**
 * Units of information.
 *
 * @author Ivan Vakhrushev
 */
public enum MemoryUnit {

    /**
     * Represents a kilobyte, equivalent to 1,024 bytes.
     */
    KB(1024L, "kilobyte"),

    /**
     * Represents a megabyte, equivalent to 1,024 kilobytes or 1,048,576 bytes.
     */
    MB(1024L * 1024L, "megabyte"),

    /**
     * Represents a gigabyte, equivalent to 1,024 megabytes or 1,073,741,824 bytes.
     */
    GB(1024L * 1024L * 1024L, "gigabyte");

    private final long dimension;
    private final String description;

    MemoryUnit(final long dimension, final String description) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return MemoryUnit.class.getSimpleName() + '{' +
            "dimension=" + dimension +
            ", description='" + description + '\'' +
            '}';
    }
}
