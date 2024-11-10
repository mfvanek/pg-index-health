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

import io.github.mfvanek.pg.model.PgContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("checkstyle:AbstractClassName")
class AbstractSkipTablesPredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> new SkipTablesPredicate(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");

        assertThatThrownBy(() -> new SkipTablesPredicate(PgContext.ofPublic(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawTableNamesToSkip cannot be null");
    }

    private static class SkipTablesPredicate extends AbstractSkipTablesPredicate {

        SkipTablesPredicate(@Nonnull final PgContext pgContext, @Nonnull final List<String> rawTableNamesToSkip) {
            super(pgContext, rawTableNamesToSkip);
        }
    }
}
