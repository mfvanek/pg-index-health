/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.List;
import java.util.function.Function;

/**
 * A utility class that provides standard checks to be performed on a specific PostgreSQL host.
 *
 * @author Ivan Vakhrushev
 * @see DatabaseCheckOnHost
 * @since 0.30.0
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
public final class StandardChecksOnHost implements Function<PgConnection, List<DatabaseCheckOnHost<? extends DbObject>>> {

    /**
     * Constructs an instance of {@code StandardChecksOnHost}.
     */
    public StandardChecksOnHost() {
        // explicitly declared constructor for javadoc
    }

    /**
     * Constructs the standard set of database checks for a given PostgreSQL connection.
     *
     * @param pgConnection the PostgreSQL connection on which the checks are to be performed
     * @return a list of database checks on the host, each representing a specific diagnostic related to {@code DbObject}
     */
    @Override
    public List<DatabaseCheckOnHost<? extends DbObject>> apply(final PgConnection pgConnection) {
        return List.of(
            new TablesWithBloatCheckOnHost(pgConnection),
            new TablesWithMissingIndexesCheckOnHost(pgConnection),
            new TablesWithoutPrimaryKeyCheckOnHost(pgConnection),
            new DuplicatedIndexesCheckOnHost(pgConnection),
            new ForeignKeysNotCoveredWithIndexCheckOnHost(pgConnection),
            new IndexesWithBloatCheckOnHost(pgConnection),
            new IndexesWithNullValuesCheckOnHost(pgConnection),
            new IntersectedIndexesCheckOnHost(pgConnection),
            new InvalidIndexesCheckOnHost(pgConnection),
            new UnusedIndexesCheckOnHost(pgConnection),
            new TablesWithoutDescriptionCheckOnHost(pgConnection),
            new ColumnsWithoutDescriptionCheckOnHost(pgConnection),
            new ColumnsWithJsonTypeCheckOnHost(pgConnection),
            new ColumnsWithSerialTypesCheckOnHost(pgConnection),
            new FunctionsWithoutDescriptionCheckOnHost(pgConnection),
            new IndexesWithBooleanCheckOnHost(pgConnection),
            new NotValidConstraintsCheckOnHost(pgConnection),
            new BtreeIndexesOnArrayColumnsCheckOnHost(pgConnection),
            new SequenceOverflowCheckOnHost(pgConnection),
            new PrimaryKeysWithSerialTypesCheckOnHost(pgConnection),
            new DuplicatedForeignKeysCheckOnHost(pgConnection),
            new IntersectedForeignKeysCheckOnHost(pgConnection),
            new PossibleObjectNameOverflowCheckOnHost(pgConnection),
            new TablesNotLinkedToOthersCheckOnHost(pgConnection),
            new ForeignKeysWithUnmatchedColumnTypeCheckOnHost(pgConnection),
            new TablesWithZeroOrOneColumnCheckOnHost(pgConnection),
            new ObjectsNotFollowingNamingConventionCheckOnHost(pgConnection),
            new ColumnsNotFollowingNamingConventionCheckOnHost(pgConnection),
            new PrimaryKeysWithVarcharCheckOnHost(pgConnection),
            new ColumnsWithFixedLengthVarcharCheckOnHost(pgConnection),
            new IndexesWithUnnecessaryWhereClauseCheckOnHost(pgConnection),
            new PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost(pgConnection),
            new ColumnsWithMoneyTypeCheckOnHost(pgConnection),
            new IndexesWithTimestampInTheMiddleCheckOnHost(pgConnection),
            new ColumnsWithTimestampOrTimetzTypeCheckOnHost(pgConnection),
            new TablesWherePrimaryKeyColumnsNotFirstCheckOnHost(pgConnection),
            new TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnHost(pgConnection)
        );
    }
}
