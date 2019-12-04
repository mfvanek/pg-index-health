/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.HighAvailabilityPgConnection;
import com.mfvanek.pg.connection.PgHost;
import com.mfvanek.pg.index.maintenance.IndexMaintenance;
import com.mfvanek.pg.index.maintenance.IndexMaintenanceFactory;
import com.mfvanek.pg.model.DuplicatedIndices;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.TableWithoutPrimaryKey;
import com.mfvanek.pg.model.UnusedIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class IndicesHealthImpl implements IndicesHealth {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndicesHealthImpl.class);

    private final IndexMaintenance maintenanceForMaster;
    private final List<IndexMaintenance> maintenanceForReplicas;

    public IndicesHealthImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                             @Nonnull final IndexMaintenanceFactory maintenanceFactory) {
        Objects.requireNonNull(haPgConnection);
        Objects.requireNonNull(maintenanceFactory);
        this.maintenanceForMaster = maintenanceFactory.forConnection(haPgConnection.getConnectionToMaster());
        this.maintenanceForReplicas = ReplicasHelper.createIndexMaintenanceForReplicas(
                haPgConnection.getConnectionsToReplicas(), maintenanceFactory);
    }

    @Nonnull
    @Override
    public List<Index> getInvalidIndices() {
        logExecutingOnMaster();
        return maintenanceForMaster.getInvalidIndices();
    }

    @Nonnull
    @Override
    public List<DuplicatedIndices> getDuplicatedIndices() {
        logExecutingOnMaster();
        return maintenanceForMaster.getDuplicatedIndices();
    }

    @Nonnull
    @Override
    public List<DuplicatedIndices> getIntersectedIndices() {
        logExecutingOnMaster();
        return maintenanceForMaster.getIntersectedIndices();
    }

    @Nonnull
    @Override
    public List<UnusedIndex> getUnusedIndices() {
        final List<List<UnusedIndex>> potentiallyUnusedIndicesFromAllHosts = new ArrayList<>();
        for (var maintenanceForReplica : maintenanceForReplicas) {
            potentiallyUnusedIndicesFromAllHosts.add(
                    doOnReplica(maintenanceForReplica.getHost(), maintenanceForReplica::getPotentiallyUnusedIndices));
        }
        return ReplicasHelper.getUnusedIndicesAsIntersectionResult(potentiallyUnusedIndicesFromAllHosts);
    }

    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        logExecutingOnMaster();
        return maintenanceForMaster.getForeignKeysNotCoveredWithIndex();
    }

    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndices() {
        final List<List<TableWithMissingIndex>> tablesWithMissingIndicesFromAllHosts = new ArrayList<>();
        for (var maintenanceForReplica : maintenanceForReplicas) {
            tablesWithMissingIndicesFromAllHosts.add(
                    doOnReplica(maintenanceForReplica.getHost(), maintenanceForReplica::getTablesWithMissingIndices));
        }
        return ReplicasHelper.getTablesWithMissingIndicesAsUnionResult(tablesWithMissingIndicesFromAllHosts);
    }

    @Nonnull
    @Override
    public List<TableWithoutPrimaryKey> getTablesWithoutPrimaryKey() {
        logExecutingOnMaster();
        return maintenanceForMaster.getTablesWithoutPrimaryKey();
    }

    @Nonnull
    @Override
    public List<IndexWithNulls> getIndicesWithNullValues() {
        logExecutingOnMaster();
        return maintenanceForMaster.getIndicesWithNullValues();
    }

    private void logExecutingOnMaster() {
        LOGGER.debug("Going to execute on master host [{}]", maintenanceForMaster.getHost().getName());
    }

    private <T> T doOnReplica(@Nonnull final PgHost host, Supplier<T> action) {
        LOGGER.debug("Going to execute on replica host [{}]", host.getName());
        return action.get();
    }
}
