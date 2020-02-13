/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
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
     * Gets bloat percentage.
     *
     * @return bloat percentage
     */
    int getBloatPercentage();
}
