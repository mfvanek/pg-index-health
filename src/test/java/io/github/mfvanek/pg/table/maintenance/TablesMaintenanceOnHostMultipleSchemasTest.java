/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.table.maintenance;

import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class TablesMaintenanceOnHostMultipleSchemasTest {

    private final TablesMaintenanceOnHost tablesMaintenance = Mockito.spy(TablesMaintenanceOnHost.class);
    private final Collection<PgContext> contexts = Arrays.asList(
            PgContext.of("demo"), PgContext.of("test"), PgContext.ofPublic());

    @Test
    void getTablesWithMissingIndexes() {
        Mockito.when(tablesMaintenance.getTablesWithMissingIndexes(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Collections.singletonList(TableWithMissingIndex.of(ctx.enrichWithSchema("t"), 1L, 100L, 2L));
                });
        final List<TableWithMissingIndex> tables = tablesMaintenance.getTablesWithMissingIndexes(contexts);
        assertThat(tables).isNotNull();
        assertThat(tables).hasSize(3);
        assertThat(tables.stream().map(TableNameAware::getTableName).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder("t", "demo.t", "test.t");
    }

    @Test
    void getTablesWithoutPrimaryKey() {
        Mockito.when(tablesMaintenance.getTablesWithoutPrimaryKey(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Arrays.asList(
                            Table.of(ctx.enrichWithSchema("t1"), 1L),
                            Table.of(ctx.enrichWithSchema("t2"), 1L));
                });
        final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey(contexts);
        assertThat(tables).isNotNull();
        assertThat(tables).hasSize(6);
        assertThat(tables.stream().map(TableNameAware::getTableName).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder("t1", "demo.t1", "test.t1", "t2", "demo.t2", "test.t2");
    }

    @Test
    void getTablesWithBloat() {
        Mockito.when(tablesMaintenance.getTablesWithBloat(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Arrays.asList(
                            TableWithBloat.of(ctx.enrichWithSchema("t1"), 100L, 45L, 45),
                            TableWithBloat.of(ctx.enrichWithSchema("t2"), 10L, 9L, 90));
                });
        final List<TableWithBloat> tables = tablesMaintenance.getTablesWithBloat(contexts);
        assertThat(tables).isNotNull();
        assertThat(tables).hasSize(6);
        assertThat(tables.stream().map(TableNameAware::getTableName).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder("t1", "demo.t1", "test.t1", "t2", "demo.t2", "test.t2");
    }
}
