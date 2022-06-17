/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.index.check.cluster.DuplicatedIndexesCheckOnCluster;
import io.github.mfvanek.pg.index.check.cluster.ForeignKeysNotCoveredWithIndexCheckOnCluster;
import io.github.mfvanek.pg.index.check.cluster.IndexesWithBloatCheckOnCluster;
import io.github.mfvanek.pg.index.check.cluster.IndexesWithNullValuesCheckOnCluster;
import io.github.mfvanek.pg.index.check.cluster.IntersectedIndexesCheckOnCluster;
import io.github.mfvanek.pg.index.check.cluster.InvalidIndexesCheckOnCluster;
import io.github.mfvanek.pg.index.check.cluster.UnusedIndexesCheckOnCluster;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.table.check.cluster.TablesWithBloatCheckOnCluster;
import io.github.mfvanek.pg.table.check.cluster.TablesWithMissingIndexesCheckOnCluster;
import io.github.mfvanek.pg.table.check.cluster.TablesWithoutPrimaryKeyCheckOnCluster;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DatabaseChecks {

    private final ConcurrentMap<Diagnostic, DatabaseCheck<?>> checks = new ConcurrentHashMap<>();

    public DatabaseChecks(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        Stream.of(
                new TablesWithBloatCheckOnCluster(haPgConnection),
                        new TablesWithMissingIndexesCheckOnCluster(haPgConnection),
                        new TablesWithoutPrimaryKeyCheckOnCluster(haPgConnection),
                        new DuplicatedIndexesCheckOnCluster(haPgConnection),
                        new ForeignKeysNotCoveredWithIndexCheckOnCluster(haPgConnection),
                        new IndexesWithBloatCheckOnCluster(haPgConnection),
                        new IndexesWithNullValuesCheckOnCluster(haPgConnection),
                        new IntersectedIndexesCheckOnCluster(haPgConnection),
                        new InvalidIndexesCheckOnCluster(haPgConnection),
                        new UnusedIndexesCheckOnCluster(haPgConnection))
                .forEach(check -> checks.putIfAbsent(check.getDiagnostic(), check));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <T extends TableNameAware> DatabaseCheck<T> getCheck(@Nonnull final Diagnostic diagnostic, @Nonnull final Class<T> type) {
        final DatabaseCheck<?> check = checks.get(diagnostic);
        if (check == null) {
            throw new IllegalStateException(String.format("Check for diagnostic %s not found", diagnostic));
        }
        if (!check.getType().isAssignableFrom(type)) {
            throw new IllegalStateException("Illegal type " + type);
        }
        return (DatabaseCheck<T>) check;
    }
}
