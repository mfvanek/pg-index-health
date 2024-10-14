/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

/**
 * Defines a type of database check.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.2
 */
public enum CheckType {
    /**
     * Static check is a check that can be run in unit/integration tests on an empty database.
     * All static checks can be performed at runtime as well.
     */
    STATIC,
    /**
     * Runtime check is a check that make sense to perform only on a production database with real data and statistics.
     * Runtime checks usually require aggregating data from all nodes in the cluster.
     *
     * @see Diagnostic.ExecutionTopology#ACROSS_CLUSTER
     */
    RUNTIME
}
