/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.core.checks.host.TablesWithMissingIndexesCheckOnHost;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Check for tables with missing indexes on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class TablesWithMissingIndexesCheckOnCluster extends AbstractCheckOnCluster<TableWithMissingIndex> {

    private static final Logger LOGGER = Logger.getLogger(TablesWithMissingIndexesCheckOnCluster.class.getName());

    /**
     * Constructs a new instance of {@code TablesWithMissingIndexesCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public TablesWithMissingIndexesCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, TablesWithMissingIndexesCheckOnHost::new, TablesWithMissingIndexesCheckOnCluster::getResultAsUnion);
    }

    static List<TableWithMissingIndex> getResultAsUnion(final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts) {
        LOGGER.fine(() -> "tablesWithMissingIndexesFromAllHosts = " + tablesWithMissingIndexesFromAllHosts);
        final List<TableWithMissingIndex> result = tablesWithMissingIndexesFromAllHosts.stream()
            .flatMap(Collection::stream)
            .distinct()
            .sorted()
            .toList();
        LOGGER.fine(() -> "Union result " + result);
        return result;
    }
}
