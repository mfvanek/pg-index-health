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

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StandardChecksOnClusterTest extends DatabaseAwareTestBase {

    private static final String[] SCHEMAS = {PgContext.DEFAULT_SCHEMA_NAME, "custom"};

    private final StandardChecksOnCluster checksFactory = new StandardChecksOnCluster();

    @Test
    @DisplayName("For each diagnostic should exist check")
    void completenessTest() {
        final List<DatabaseCheckOnCluster<? extends @NonNull DbObject>> checks = checksFactory.apply(getHaPgConnection());
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());
        final Set<String> checkNames = checks.stream()
            .map(DatabaseCheckOnCluster::getName)
            .collect(Collectors.toUnmodifiableSet());
        assertThat(checkNames)
            .hasSameSizeAs(Diagnostic.values());
    }

    @Test
    @DisplayName("Each check should return nothing on empty database")
    void onEmptyDatabaseEachCheckShouldReturnNothing() {
        for (final DatabaseCheckOnCluster<? extends @NonNull DbObject> check : checksFactory.apply(getHaPgConnection())) {
            assertThat(check.check())
                .isEmpty();
        }
    }

    @Test
    void onDatabaseWithoutThemCheckShouldReturnNothing() {
        final Set<String> exclusions = Stream.of(
                Diagnostic.BLOATED_INDEXES,
                Diagnostic.BLOATED_TABLES,
                Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX,
                Diagnostic.COLUMNS_WITH_FIXED_LENGTH_VARCHAR,
                Diagnostic.COLUMNS_WITH_CHAR_TYPE
            )
            .map(Diagnostic::getName)
            .collect(Collectors.toUnmodifiableSet());
        for (final String schemaName : SCHEMAS) {
            executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withCommentOnColumns().withCommentOnTables(), ctx -> {
                for (final DatabaseCheckOnCluster<? extends @NonNull DbObject> check : checksFactory.apply(getHaPgConnection())) {
                    if (!exclusions.contains(check.getName())) {
                        assertThat(check.check(ctx))
                            .isEmpty();
                    }
                }
            });
        }
    }
}
