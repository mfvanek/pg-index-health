/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

class SkipLiquibaseTablesPredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenPgContextIsNull() {
        assertThatThrownBy(() -> SkipLiquibaseTablesPredicate.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");
    }

    @Test
    void shouldWorkWithDbObjectsList() {
        final List<? extends DbObject> objects = List.of(
            Table.of("t", 0L),
            Table.of("databasechangelog", 0L)
        );
        assertThat(objects.stream().filter(SkipLiquibaseTablesPredicate.ofPublic()))
            .hasSize(1)
            .asInstanceOf(list(Table.class))
            .containsExactly(Table.of("t", 0L));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithCustomSchema(final String schemaName) {
        final PgContext ctx = PgContext.of(schemaName);
        assertThat(SkipLiquibaseTablesPredicate.of(ctx))
            .accepts(Table.of(ctx.enrichWithSchema("t"), 0L))
            .accepts(Index.of(ctx.enrichWithSchema("t"), ctx.enrichWithSchema("i")))
            .accepts(SequenceState.of(ctx.enrichWithSchema("s"), "int", 100.0))
            .rejects(Table.of(ctx.enrichWithSchema("databasechangelog"), 0L))
            .rejects(Table.of(ctx.enrichWithSchema("DATABASECHANGELOG"), 0L))
            .rejects(Table.of(ctx.enrichWithSchema("databasechangeloglock"), 0L))
            .rejects(Table.of(ctx.enrichWithSchema("DATABASECHANGELOGLOCK"), 0L));
    }
}
