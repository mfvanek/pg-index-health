/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.common;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@Tag("fast")
class DatabaseCheckOnClusterTest {

    private static final Collection<PgContext> CONTEXTS = List.of(
        PgContext.of("demo"), PgContext.of("test"), PgContext.ofDefault());

    @SuppressWarnings("unchecked")
    @Test
    void check() {
        final DatabaseCheckOnCluster<Table> check = (DatabaseCheckOnCluster<@NonNull Table>) Mockito.spy(DatabaseCheckOnCluster.class);
        Mockito.when(check.check(any(PgContext.class), any()))
            .thenAnswer(invocation -> {
                final PgContext ctx = invocation.getArgument(0);
                return List.of(
                    Table.of(ctx, "t1", 1L),
                    Table.of(ctx, "t2", 1L));
            });
        final List<Table> tables = check.check(CONTEXTS, item -> true);
        assertThat(tables)
            .hasSize(6)
            .extracting(Table::getTableName)
            .containsExactlyInAnyOrder("t1", "demo.t1", "test.t1", "t2", "demo.t2", "test.t2");
    }
}
