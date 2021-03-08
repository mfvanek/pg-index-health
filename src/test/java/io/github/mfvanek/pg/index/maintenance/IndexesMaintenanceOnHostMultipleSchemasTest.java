/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.IndexWithSize;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.TableNameAware;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

class IndexesMaintenanceOnHostMultipleSchemasTest {

    private final IndexesMaintenanceOnHost indexesMaintenance = Mockito.spy(IndexesMaintenanceOnHost.class);
    private final Collection<PgContext> contexts = Arrays.asList(
            PgContext.of("demo"), PgContext.of("test"), PgContext.ofPublic());

    @Test
    void getInvalidIndexes() {
        Mockito.when(indexesMaintenance.getInvalidIndexes(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Collections.singletonList(
                            Index.of(ctx.enrichWithSchema("t"), ctx.enrichWithSchema("i1")));
                });
        final List<Index> indexes = indexesMaintenance.getInvalidIndexes(contexts);
        assertNotNull(indexes);
        assertThat(indexes, hasSize(3));
        assertThat(indexes.stream()
                .map(TableNameAware::getTableName)
                .collect(Collectors.toSet()), containsInAnyOrder("t", "demo.t", "test.t"));
    }

    @Test
    void getDuplicatedIndexes() {
        Mockito.when(indexesMaintenance.getDuplicatedIndexes(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Collections.singletonList(DuplicatedIndexes.of(
                            IndexWithSize.of(ctx.enrichWithSchema("t"), ctx.enrichWithSchema("i1"), 1L),
                            IndexWithSize.of(ctx.enrichWithSchema("t"), ctx.enrichWithSchema("i2"), 1L)));
                });
        final List<DuplicatedIndexes> indexes = indexesMaintenance.getDuplicatedIndexes(contexts);
        assertNotNull(indexes);
        assertThat(indexes, hasSize(3));
        assertThat(indexes.stream()
                .map(TableNameAware::getTableName)
                .collect(Collectors.toSet()), containsInAnyOrder("t", "demo.t", "test.t"));
    }

    @Test
    void getIntersectedIndexes() {
        Mockito.when(indexesMaintenance.getIntersectedIndexes(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Arrays.asList(
                            DuplicatedIndexes.of(
                                    IndexWithSize.of(ctx.enrichWithSchema("t1"), ctx.enrichWithSchema("i1"), 1L),
                                    IndexWithSize.of(ctx.enrichWithSchema("t1"), ctx.enrichWithSchema("i2"), 2L)),
                            DuplicatedIndexes.of(
                                    IndexWithSize.of(ctx.enrichWithSchema("t2"), ctx.enrichWithSchema("i3"), 3L),
                                    IndexWithSize.of(ctx.enrichWithSchema("t2"), ctx.enrichWithSchema("i4"), 4L)));
                });
        final List<DuplicatedIndexes> indexes = indexesMaintenance.getIntersectedIndexes(contexts);
        assertNotNull(indexes);
        assertThat(indexes, hasSize(6));
        assertThat(indexes.stream()
                .map(TableNameAware::getTableName)
                .collect(Collectors.toSet()), containsInAnyOrder("t1", "demo.t1", "test.t1", "t2", "demo.t2", "test.t2"));
    }

    @Test
    void getUnusedIndexes() {
        Mockito.when(indexesMaintenance.getUnusedIndexes(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Arrays.asList(
                            UnusedIndex.of(ctx.enrichWithSchema("t1"), ctx.enrichWithSchema("i1"), 1L, 1L),
                            UnusedIndex.of(ctx.enrichWithSchema("t2"), ctx.enrichWithSchema("i2"), 2L, 0L));
                });
        final List<UnusedIndex> indexes = indexesMaintenance.getUnusedIndexes(contexts);
        assertNotNull(indexes);
        assertThat(indexes, hasSize(6));
        assertThat(indexes.stream()
                .map(TableNameAware::getTableName)
                .collect(Collectors.toSet()), containsInAnyOrder("t1", "demo.t1", "test.t1", "t2", "demo.t2", "test.t2"));
    }

    @Test
    void getForeignKeysNotCoveredWithIndex() {
        Mockito.when(indexesMaintenance.getForeignKeysNotCoveredWithIndex(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Arrays.asList(
                            ForeignKey.ofColumn(ctx.enrichWithSchema("t1"), "f1", "col1"),
                            ForeignKey.ofColumn(ctx.enrichWithSchema("t1"), "f2", "col2"));
                });
        final List<ForeignKey> foreignKeys = indexesMaintenance.getForeignKeysNotCoveredWithIndex(contexts);
        assertNotNull(foreignKeys);
        assertThat(foreignKeys, hasSize(6));
        assertThat(foreignKeys.stream()
                .map(TableNameAware::getTableName)
                .collect(Collectors.toSet()), containsInAnyOrder("t1", "demo.t1", "test.t1"));
    }

    @Test
    void getIndexesWithNullValues() {
        Mockito.when(indexesMaintenance.getIndexesWithNullValues(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Collections.singletonList(
                            IndexWithNulls.of(ctx.enrichWithSchema("t1"), ctx.enrichWithSchema("i1"), 1L, "col1"));
                });
        final List<IndexWithNulls> indexes = indexesMaintenance.getIndexesWithNullValues(contexts);
        assertNotNull(indexes);
        assertThat(indexes, hasSize(3));
        assertThat(indexes.stream()
                .map(TableNameAware::getTableName)
                .collect(Collectors.toSet()), containsInAnyOrder("t1", "demo.t1", "test.t1"));
    }

    @Test
    void getIndexesWithBloat() {
        Mockito.when(indexesMaintenance.getIndexesWithBloat(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Collections.singletonList(
                            IndexWithBloat.of(ctx.enrichWithSchema("t1"), ctx.enrichWithSchema("i1"), 100L, 30L, 30));
                });
        final List<IndexWithBloat> indexes = indexesMaintenance.getIndexesWithBloat(contexts);
        assertNotNull(indexes);
        assertThat(indexes, hasSize(3));
        assertThat(indexes.stream()
                .map(TableNameAware::getTableName)
                .collect(Collectors.toSet()), containsInAnyOrder("t1", "demo.t1", "test.t1"));
    }
}
