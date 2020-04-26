/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.PgContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

class IndexMaintenanceMultipleSchemasTest {

    private final IndexMaintenance indexMaintenance = Mockito.spy(IndexMaintenance.class);
    private final Collection<PgContext> contexts = Arrays.asList(
            PgContext.of("demo"), PgContext.of("test"), PgContext.ofPublic());

    @Test
    void getInvalidIndexesShouldWork() {
        Mockito.when(indexMaintenance.getInvalidIndexes(any(PgContext.class)))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(0);
                    return Collections.singletonList(
                            Index.of(ctx.enrichWithSchema("t"), ctx.enrichWithSchema("i1")));
                });
        /*
        Mockito.when(indexMaintenance.getDuplicatedIndexes(any()))
                .thenAnswer(invocation -> {
                    final PgContext ctx = invocation.getArgument(1);
                    return Collections.singletonList(DuplicatedIndexes.of(
                            IndexWithSize.of(ctx.enrichWithSchema("t"), ctx.enrichWithSchema("i1"), 1L),
                            IndexWithSize.of(ctx.enrichWithSchema("t"), ctx.enrichWithSchema("i2"), 1L)));
                });
*/
        final List<Index> indexes = indexMaintenance.getInvalidIndexes(contexts);
        assertNotNull(indexes);
        assertThat(indexes, hasSize(3));
    }
}
