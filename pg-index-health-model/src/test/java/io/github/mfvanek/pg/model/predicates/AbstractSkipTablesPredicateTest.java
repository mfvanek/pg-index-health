/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("checkstyle:AbstractClassName")
class AbstractSkipTablesPredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> new SkipTablesPredicate(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");

        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> new SkipTablesPredicate(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");
    }

    @Test
    void canCombinePredicatesIntoChain() {
        final Predicate<DbObject> composite = SkipLiquibaseTablesPredicate.ofDefault().and(SkipFlywayTablesPredicate.ofDefault());
        assertThat(composite)
            .accepts(Table.of("t"))
            .rejects(Table.of("databasechangelog", 1L))
            .rejects(Table.of("flyway_schema_history", 1L));
    }

    @Test
    void shouldNotCastObjectsWhenExclusionsIsEmpty() {
        final Table mockTable = Mockito.mock(Table.class);
        assertThat(new SkipTablesPredicate(PgContext.ofDefault(), List.of()))
            .accepts(mockTable);
        Mockito.verify(mockTable, Mockito.never()).getTableName();
    }

    private static class SkipTablesPredicate extends AbstractSkipTablesPredicate {

        SkipTablesPredicate(final PgContext pgContext, final Collection<String> rawTableNamesToSkip) {
            super(pgContext, rawTableNamesToSkip);
        }
    }
}
