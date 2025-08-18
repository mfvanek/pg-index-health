/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.bloat;

/**
 * Allows getting information about bloat in a database.
 *
 * @author Ivan Vakhrushev
 */
public interface BloatAware {

    /**
     * Constant representing the field name for bloat size in bytes.
     */
    String BLOAT_SIZE_IN_BYTES_FIELD = "bloatSizeInBytes";
    /**
     * Constant representing the field name for bloat percentage.
     */
    String BLOAT_PERCENTAGE_FIELD = "bloatPercentage";

    /**
     * Retrieves bloat amount in bytes.
     *
     * @return bloat amount
     */
    long getBloatSizeInBytes();

    /**
     * Retrieves bloat percentage (in the range from 0 to 100 inclusive).
     *
     * @return bloat percentage
     */
    double getBloatPercentage();
}
