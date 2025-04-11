/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.health.checks.cluster.BtreeIndexesOnArrayColumnsCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.ColumnsWithJsonTypeCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.ColumnsWithSerialTypesCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.ColumnsWithoutDescriptionCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.DuplicatedForeignKeysCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.DuplicatedIndexesCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.ForeignKeysNotCoveredWithIndexCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.ForeignKeysWithUnmatchedColumnTypeCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.FunctionsWithoutDescriptionCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.IndexesWithBloatCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.IndexesWithBooleanCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.IndexesWithNullValuesCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.IntersectedForeignKeysCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.IntersectedIndexesCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.InvalidIndexesCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.NotValidConstraintsCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.ObjectsNotFollowingNamingConventionCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.PossibleObjectNameOverflowCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.PrimaryKeysWithSerialTypesCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.SequenceOverflowCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.TablesNotLinkedToOthersCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.TablesWithBloatCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.TablesWithMissingIndexesCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.TablesWithZeroOrOneColumnCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.TablesWithoutDescriptionCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.TablesWithoutPrimaryKeyCheckOnCluster;
import io.github.mfvanek.pg.health.checks.cluster.UnusedIndexesCheckOnCluster;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A thread-safe class that aggregates various database checks on a PostgreSQL cluster.
 * <p>
 * This class initializes a list of database checks to be performed on the cluster,
 * such as checks for bloat, missing indexes, unused indexes, invalid constraints,
 * and other structural and metadata issues.
 * <p>
 * Each check is represented by an instance of {@link DatabaseCheckOnCluster} or its subclasses,
 * and they are executed against the database cluster using a shared {@link HighAvailabilityPgConnection}.
 *
 * @author Ivan Vakhrushev
 * @see DatabaseCheckOnCluster
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
@ThreadSafe
public final class DatabaseChecksOnCluster {

    private final List<DatabaseCheckOnCluster<? extends DbObject>> checks;

    /**
     * Constructs an instance of {@code DatabaseChecksOnCluster} and initializes
     * a predefined list of checks to be performed on the PostgreSQL cluster.
     *
     * @param haPgConnection a high-availability PostgreSQL connection used for performing the checks
     * @throws NullPointerException if {@code haPgConnection} is null
     */
    public DatabaseChecksOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        this.checks = List.of(
            new TablesWithBloatCheckOnCluster(haPgConnection),
            new TablesWithMissingIndexesCheckOnCluster(haPgConnection),
            new TablesWithoutPrimaryKeyCheckOnCluster(haPgConnection),
            new DuplicatedIndexesCheckOnCluster(haPgConnection),
            new ForeignKeysNotCoveredWithIndexCheckOnCluster(haPgConnection),
            new IndexesWithBloatCheckOnCluster(haPgConnection),
            new IndexesWithNullValuesCheckOnCluster(haPgConnection),
            new IntersectedIndexesCheckOnCluster(haPgConnection),
            new InvalidIndexesCheckOnCluster(haPgConnection),
            new UnusedIndexesCheckOnCluster(haPgConnection),
            new TablesWithoutDescriptionCheckOnCluster(haPgConnection),
            new ColumnsWithoutDescriptionCheckOnCluster(haPgConnection),
            new ColumnsWithJsonTypeCheckOnCluster(haPgConnection),
            new ColumnsWithSerialTypesCheckOnCluster(haPgConnection),
            new FunctionsWithoutDescriptionCheckOnCluster(haPgConnection),
            new IndexesWithBooleanCheckOnCluster(haPgConnection),
            new NotValidConstraintsCheckOnCluster(haPgConnection),
            new BtreeIndexesOnArrayColumnsCheckOnCluster(haPgConnection),
            new SequenceOverflowCheckOnCluster(haPgConnection),
            new PrimaryKeysWithSerialTypesCheckOnCluster(haPgConnection),
            new DuplicatedForeignKeysCheckOnCluster(haPgConnection),
            new IntersectedForeignKeysCheckOnCluster(haPgConnection),
            new PossibleObjectNameOverflowCheckOnCluster(haPgConnection),
            new TablesNotLinkedToOthersCheckOnCluster(haPgConnection),
            new ForeignKeysWithUnmatchedColumnTypeCheckOnCluster(haPgConnection),
            new TablesWithZeroOrOneColumnCheckOnCluster(haPgConnection),
            new ObjectsNotFollowingNamingConventionCheckOnCluster(haPgConnection)
        );
    }

    /**
     * Returns the list of all configured database checks.
     *
     * @return an immutable list of {@link DatabaseCheckOnCluster} instances
     */
    @Nonnull
    public List<DatabaseCheckOnCluster<? extends DbObject>> getAll() {
        return checks;
    }
}
