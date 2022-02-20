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

/**
 * Allows to get information about bloat in database.
 *
 * @author Ivan Vakhrushev
 */
public interface BloatAware {

    /**
     * Gets bloat amount in bytes.
     *
     * @return bloat amount
     */
    long getBloatSizeInBytes();

    /**
     * Gets bloat percentage (in the range from 0 to 100 inclusive).
     *
     * @return bloat percentage
     */
    int getBloatPercentage();
}
