/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.index.maintenance.IndexMaintenance;
import com.mfvanek.pg.index.maintenance.IndexMaintenanceFactory;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.UnusedIndex;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class ReplicasHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicasHelper.class);

    private ReplicasHelper() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    static List<IndexMaintenance> createIndexMaintenanceForReplicas(@Nonnull final Set<PgConnection> connectionsToReplicas,
                                                                    @Nonnull final IndexMaintenanceFactory maintenanceFactory) {
        final List<IndexMaintenance> result = new ArrayList<>(connectionsToReplicas.size());
        result.addAll(
                connectionsToReplicas.stream()
                        .map(maintenanceFactory::forConnection)
                        .collect(Collectors.toList())
        );
        return result;
    }

    @Nonnull
    static List<UnusedIndex> getUnusedIndexesAsIntersectionResult(
            @Nonnull final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts) {
        LOGGER.debug("potentiallyUnusedIndexesFromAllHosts = {}", potentiallyUnusedIndexesFromAllHosts);
        Collection<UnusedIndex> unusedIndexes = null;
        for (var unusedIndexesFromHost : potentiallyUnusedIndexesFromAllHosts) {
            if (unusedIndexes == null) {
                unusedIndexes = unusedIndexesFromHost;
            }
            unusedIndexes = CollectionUtils.intersection(unusedIndexes, unusedIndexesFromHost);
        }
        final List<UnusedIndex> result = unusedIndexes == null ? Collections.emptyList() : List.copyOf(unusedIndexes);
        LOGGER.debug("Intersection result {}", result);
        return result;
    }

    @Nonnull
    static List<TableWithMissingIndex> getTablesWithMissingIndexesAsUnionResult(
            @Nonnull final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts) {
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
