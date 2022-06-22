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

import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class DatabaseCheckOnClusterTest {

    private static final Collection<PgContext> CONTEXTS = Arrays.asList(
            PgContext.of("demo"), PgContext.of("test"), PgContext.ofPublic());

    @SuppressWarnings("unchecked")
    @Test
    void check() {
        final DatabaseCheckOnCluster<Table> check = (DatabaseCheckOnCluster<Table>) Mockito.spy(DatabaseCheckOnCluster.class);
        Mockito.when(check.check(any(PgContext.class), any()))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Arrays.asList(
                            Table.of(ctx.enrichWithSchema("t1"), 1L),
                            Table.of(ctx.enrichWithSchema("t2"), 1L));
                });
        final List<Table> tables = check.check(CONTEXTS, item -> true);
        assertThat(tables)
                .isNotNull()
                .hasSize(6);
        assertThat(tables.stream().map(TableNameAware::getTableName).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder("t1", "demo.t1", "test.t1", "t2", "demo.t2", "test.t2");
    }
}
