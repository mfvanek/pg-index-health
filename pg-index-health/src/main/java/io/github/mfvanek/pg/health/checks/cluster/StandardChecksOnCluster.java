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
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.List;
import java.util.function.Function;

/**
 * A utility class that provides standard checks to be performed across a PostgreSQL cluster.
 *
 * @author Ivan Vakhrushev
 * @see DatabaseCheckOnCluster
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
public final class StandardChecksOnCluster implements Function<HighAvailabilityPgConnection, List<DatabaseCheckOnCluster<? extends DbObject>>> {

    /**
     * Constructs an instance of {@code StandardChecksOnCluster}.
     */
    public StandardChecksOnCluster() {
        // explicitly declared constructor for javadoc
    }

    /**
     * Constructs the standard set of database checks for a given high-availability PostgreSQL connection.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     * @return a list of database checks specific to a PostgreSQL cluster, where each check is an instance of {@code DatabaseCheckOnCluster<? extends DbObject>}
     */
    @Override
    public List<DatabaseCheckOnCluster<? extends DbObject>> apply(final HighAvailabilityPgConnection haPgConnection) {
        return List.of(
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
            new ObjectsNotFollowingNamingConventionCheckOnCluster(haPgConnection),
            new ColumnsNotFollowingNamingConventionCheckOnCluster(haPgConnection),
            new PrimaryKeysWithVarcharCheckOnCluster(haPgConnection),
            new ColumnsWithFixedLengthVarcharCheckOnCluster(haPgConnection),
            new IndexesWithUnnecessaryWhereClauseCheckOnCluster(haPgConnection),
            new PrimaryKeysThatMostLikelyNaturalKeysCheckOnCluster(haPgConnection),
            new ColumnsWithMoneyTypeCheckOnCluster(haPgConnection),
            new IndexesWithTimestampInTheMiddleCheckOnCluster(haPgConnection),
            new ColumnsWithTimestampOrTimetzTypeCheckOnCluster(haPgConnection),
            new TablesWherePrimaryKeyColumnsNotFirstCheckOnCluster(haPgConnection),
            new TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnCluster(haPgConnection)
        );
    }
}
