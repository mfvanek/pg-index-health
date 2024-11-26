/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseChecksOnClusterTest extends DatabaseAwareTestBase {

    private static final String[] SCHEMAS = {PgContext.DEFAULT_SCHEMA_NAME, "custom"};

    private final DatabaseChecksOnCluster checksOnCluster = new DatabaseChecksOnCluster(getHaPgConnection());

    @Test
    @DisplayName("For each diagnostic should exist check")
    void completenessTest() {
        final List<DatabaseCheckOnCluster<? extends DbObject>> checks = checksOnCluster.getAll();
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());
        final Set<Diagnostic> diagnostics = checks.stream()
            .map(DatabaseCheckOnCluster::getDiagnostic)
            .collect(Collectors.toUnmodifiableSet());
        assertThat(diagnostics)
            .hasSameSizeAs(Diagnostic.values());
    }

    @Test
    @DisplayName("Each check should return nothing on empty database")
    void onEmptyDatabaseEachCheckShouldReturnNothing() {
        for (final DatabaseCheckOnCluster<? extends DbObject> check : checksOnCluster.getAll()) {
            assertThat(check.check())
                .isEmpty();
        }
    }

    @Test
    void onDatabaseWithoutThemCheckShouldReturnNothing() {
        final Set<Diagnostic> exclusions = EnumSet.of(Diagnostic.BLOATED_INDEXES, Diagnostic.BLOATED_TABLES, Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX);
        for (final String schemaName : SCHEMAS) {
            for (final DatabaseCheckOnCluster<? extends DbObject> check : checksOnCluster.getAll()) {
                if (!exclusions.contains(check.getDiagnostic())) {
                    executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withCommentOnColumns().withCommentOnTables(),
                        ctx ->
                            assertThat(check.check(ctx))
                                .isEmpty());
                }
            }
        }
    }
}
