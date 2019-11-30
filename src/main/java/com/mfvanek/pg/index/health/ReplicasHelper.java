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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class ReplicasHelper {

    private ReplicasHelper() {
        throw new UnsupportedOperationException();
    }

    static List<IndexMaintenance> createIndexMaintenanceForReplicas(@Nonnull final PgConnection pgConnection,
                                                                    @Nonnull final IndexMaintenanceFactory maintenanceFactory) {
        final List<IndexMaintenance> result = new ArrayList<>(pgConnection.getReplicasCount());
        result.addAll(
                pgConnection.getReplicasDataSource().stream()
                        .map(maintenanceFactory::forDataSource)
                        .collect(Collectors.toList())
        );
        return result;
    }

    static List<UnusedIndex> getUnusedIndicesAsIntersectionResult(
            @Nonnull final List<List<UnusedIndex>> potentiallyUnusedIndicesFromAllHosts) {
        Collection<UnusedIndex> unusedIndices = null;
        for (var unusedIndicesFromHost : potentiallyUnusedIndicesFromAllHosts) {
            if (unusedIndices == null) {
                unusedIndices = unusedIndicesFromHost;
            }
            unusedIndices = CollectionUtils.intersection(unusedIndices, unusedIndicesFromHost);
        }
        return unusedIndices == null ? Collections.emptyList() : List.copyOf(unusedIndices);
    }

    static List<TableWithMissingIndex> getTablesWithMissingIndicesAsUnionResult(
            @Nonnull final List<List<TableWithMissingIndex>> tablesWithMissingIndicesFromAllHosts) {
        return tablesWithMissingIndicesFromAllHosts.stream()
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
