/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithType;
import io.github.mfvanek.pg.model.context.PgContext;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class ColumnsWithBlobTypeCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull ColumnWithType> check = new ColumnsWithBlobTypeCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ColumnWithType.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_BLOB_TYPE)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withBlobTypeColumns, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithType.of(Column.ofNotNull(ctx, "\"document-bad\"", "\"content-bad\""), "oid"),
                    ColumnWithType.of(Column.ofNullable(ctx, "image", "raster"), "lo"),
                    ColumnWithType.of(Column.ofNotNull(ctx, "media_file", "full_image"), "lo"),
                    ColumnWithType.of(Column.ofNullable(ctx, "media_file", "thumbnail"), "oid")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withBlobTypeColumnInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithType.of(Column.ofNullable(ctx, "attachment", "file_data"), "oid")
                ));
    }
}
