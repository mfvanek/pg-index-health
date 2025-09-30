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
 * Allows getting information about the execution topology.
 *
 * @author Ivan Vakhrushev
 * @see ExecutionTopology
 * @since 0.30.0
 */
public interface TopologyAware {

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
