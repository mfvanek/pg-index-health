/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.checks.cluster.BtreeIndexesOnArrayColumnsCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.ColumnsWithJsonTypeCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.ColumnsWithSerialTypesCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.ColumnsWithoutDescriptionCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.DuplicatedIndexesCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.ForeignKeysNotCoveredWithIndexCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.FunctionsWithoutDescriptionCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.IndexesWithBloatCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.IndexesWithBooleanCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.IndexesWithNullValuesCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.IntersectedIndexesCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.InvalidIndexesCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.NotValidConstraintsCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.TablesWithBloatCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.TablesWithMissingIndexesCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.TablesWithoutDescriptionCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.TablesWithoutPrimaryKeyCheckOnCluster;
import io.github.mfvanek.pg.checks.cluster.UnusedIndexesCheckOnCluster;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.model.DbObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity"})
@ThreadSafe
public class DatabaseChecks {

    private final ConcurrentMap<Diagnostic, DatabaseCheckOnCluster<? extends DbObject>> checks = new ConcurrentHashMap<>();

    public DatabaseChecks(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        final Stream<DatabaseCheckOnCluster<? extends DbObject>> allChecks = Stream.of(
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
            new BtreeIndexesOnArrayColumnsCheckOnCluster(haPgConnection)
        );
        allChecks.forEach(check -> this.checks.putIfAbsent(check.getDiagnostic(), check));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <T extends DbObject> DatabaseCheckOnCluster<T> getCheck(@Nonnull final Diagnostic diagnostic, @Nonnull final Class<T> type) {
        final DatabaseCheckOnCluster<?> check = checks.get(diagnostic);
        if (check == null) {
            throw new IllegalStateException(String.format("Check for diagnostic %s not found", diagnostic));
        }
        if (!type.isAssignableFrom(check.getType())) {
            throw new IllegalArgumentException("Illegal type: " + type);
        }
        return (DatabaseCheckOnCluster<T>) check;
    }
}
