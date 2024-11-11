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

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

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

        final PgContext ctx = PgContext.ofPublic();
        assertThatThrownBy(() -> new SkipTablesPredicate(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawTableNamesToSkip cannot be null");
    }

    @Test
    void canCombinePredicatesIntoChain() {
        final Predicate<DbObject> composite = SkipLiquibaseTablesPredicate.ofPublic().and(SkipFlywayTablesPredicate.ofPublic());
        assertThat(composite)
            .accepts(Table.of("t", 0L))
            .rejects(Table.of("databasechangelog", 1L))
            .rejects(Table.of("flyway_schema_history", 1L));
    }

    private static class SkipTablesPredicate extends AbstractSkipTablesPredicate {

        SkipTablesPredicate(@Nonnull final PgContext pgContext, @Nonnull final List<String> rawTableNamesToSkip) {
            super(pgContext, rawTableNamesToSkip);
        }
    }
}
