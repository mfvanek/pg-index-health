/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.host.TablesWithMissingIndexesCheckOnHost;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * Check for tables with missing indexes on all hosts in the cluster.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
public class TablesWithMissingIndexesCheckOnCluster extends AbstractCheckOnCluster<TableWithMissingIndex> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TablesWithMissingIndexesCheckOnCluster.class);

    public TablesWithMissingIndexesCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, TablesWithMissingIndexesCheckOnHost::new, TablesWithMissingIndexesCheckOnCluster::getResultAsUnion);
    }

    @Nonnull
    static List<TableWithMissingIndex> getResultAsUnion(@Nonnull final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts) {
        LOGGER.debug("tablesWithMissingIndexesFromAllHosts = {}", tablesWithMissingIndexesFromAllHosts);
        final List<TableWithMissingIndex> result = tablesWithMissingIndexesFromAllHosts.stream()
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        LOGGER.debug("Union result {}", result);
        return result;
    }
}
