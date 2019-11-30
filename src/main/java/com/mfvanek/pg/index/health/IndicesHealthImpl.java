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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndicesHealthImpl implements IndicesHealth {

    private final IndexMaintenance maintenanceForMaster;
    private final List<IndexMaintenance> maintenanceForReplicas;

    public IndicesHealthImpl(@Nonnull final PgConnection pgConnection,
                             @Nonnull final IndexMaintenanceFactory maintenanceFactory) {
        Objects.requireNonNull(pgConnection);
        Objects.requireNonNull(maintenanceFactory);
        this.maintenanceForMaster = maintenanceFactory.forDataSource(pgConnection.getMasterDataSource());
        this.maintenanceForReplicas = ReplicasHelper.createIndexMaintenanceForReplicas(pgConnection, maintenanceFactory);
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
        final List<List<UnusedIndex>> potentiallyUnusedIndicesFromAllHosts = new ArrayList<>();
        // From master
        potentiallyUnusedIndicesFromAllHosts.add(maintenanceForMaster.getPotentiallyUnusedIndices());
        // And all replicas
        for (var maintenanceForReplica : maintenanceForReplicas) {
            potentiallyUnusedIndicesFromAllHosts.add(maintenanceForReplica.getPotentiallyUnusedIndices());
        }
        return ReplicasHelper.getUnusedIndicesAsIntersectionResult(potentiallyUnusedIndicesFromAllHosts);
    }

    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return maintenanceForMaster.getForeignKeysNotCoveredWithIndex();
    }

    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndices() {
        final List<List<TableWithMissingIndex>> tablesWithMissingIndicesFromAllHosts = new ArrayList<>();
        // From master
        tablesWithMissingIndicesFromAllHosts.add(maintenanceForMaster.getTablesWithMissingIndices());
        // And all replicas
        for (var maintenanceForReplica : maintenanceForReplicas) {
            tablesWithMissingIndicesFromAllHosts.add(maintenanceForReplica.getTablesWithMissingIndices());
        }
        return ReplicasHelper.getTablesWithMissingIndicesAsUnionResult(tablesWithMissingIndicesFromAllHosts);
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
