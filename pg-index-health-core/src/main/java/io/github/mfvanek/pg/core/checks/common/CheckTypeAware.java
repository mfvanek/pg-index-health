/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

/**
 * Allows getting information about check (diagnostic) type.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.2
 */
public interface CheckTypeAware extends CheckNameAware {

    /**
     * Defines whether this check/diagnostic is runtime (make sense to perform only on a production database with real data and statistics).
     *
     * @return true if this is a runtime check
     * @see ExecutionTopology#ACROSS_CLUSTER
     */
    boolean isRuntime();

    /**
     * Defines whether this check/diagnostic is static (can be run in unit/integration tests on an empty database).
     *
     * @return true if this is a static check
     */
    default boolean isStatic() {
        return !isRuntime();
    }

    /**
     * Retrieves the place where the diagnostic should be executed.
     *
     * @return {@code ExecutionTopology}
     */
    ExecutionTopology getExecutionTopology();

    /**
     * Shows whether diagnostic results should be collected from all nodes in the cluster.
     *
     * @return true if diagnostic results should be collected from all nodes in the cluster
     */
    default boolean isAcrossCluster() {
        return getExecutionTopology() == ExecutionTopology.ACROSS_CLUSTER;
    }
}
