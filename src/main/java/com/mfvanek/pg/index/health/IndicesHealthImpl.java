/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.index.maintenance.IndexMaintenance;
import com.mfvanek.pg.index.maintenance.IndexMaintenanceFactory;
import com.mfvanek.pg.model.DuplicatedIndices;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.TableWithoutPrimaryKey;
import com.mfvanek.pg.model.UnusedIndex;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndicesHealthImpl implements IndicesHealth {

    private final IndexMaintenance maintenanceForMaster;
    private final List<IndexMaintenance> maintenanceForReplicas;

    public IndicesHealthImpl(@Nonnull final PgConnection pgConnection,
                             @Nonnull final IndexMaintenanceFactory maintenanceFactory) {
        Objects.requireNonNull(pgConnection);
        Objects.requireNonNull(maintenanceFactory);
        this.maintenanceForMaster = maintenanceFactory.forDataSource(pgConnection.getMasterDataSource());
        this.maintenanceForReplicas = new ArrayList<>(pgConnection.getReplicasCount());
        this.maintenanceForReplicas.addAll(
                pgConnection.getReplicasDataSource().stream()
                        .map(maintenanceFactory::forDataSource)
                        .collect(Collectors.toList())
        );
    }

    @Nonnull
    @Override
    public List<Index> getInvalidIndices() {
        return maintenanceForMaster.getInvalidIndices();
    }

    @Nonnull
    @Override
    public List<DuplicatedIndices> getDuplicatedIndices() {
        return maintenanceForMaster.getDuplicatedIndices();
    }

    @Nonnull
    @Override
    public List<DuplicatedIndices> getIntersectedIndices() {
        return maintenanceForMaster.getIntersectedIndices();
    }

    @Nonnull
    @Override
    public List<UnusedIndex> getUnusedIndices() {
        Collection<UnusedIndex> unusedIndices = maintenanceForMaster.getPotentiallyUnusedIndices();
        for (var maintenanceForReplica : maintenanceForReplicas) {
            final var unusedIndicesFromReplica = maintenanceForReplica.getPotentiallyUnusedIndices();
            unusedIndices = CollectionUtils.intersection(unusedIndices, unusedIndicesFromReplica);
        }
        return List.copyOf(unusedIndices);
    }

    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return maintenanceForMaster.getForeignKeysNotCoveredWithIndex();
    }

    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndices() {
        return Stream.concat(
                maintenanceForMaster.getTablesWithMissingIndices().stream(),
                maintenanceForReplicas.stream()
                        .flatMap(m -> m.getTablesWithMissingIndices().stream()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey() {
        return maintenanceForMaster.getTablesWithoutPrimaryKey();
    }

    @Nonnull
    @Override
    public List<IndexWithNulls> getIndicesWithNullValues() {
        return maintenanceForMaster.getIndicesWithNullValues();
    }
}
