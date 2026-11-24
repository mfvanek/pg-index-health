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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipBySequenceNamePredicate;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class SequenceOverflowCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull SequenceState> check = new SequenceOverflowCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(SequenceState.class)
            .hasDiagnostic(Diagnostic.SEQUENCE_OVERFLOW)
            .isRuntime();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithSequences(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSequenceOverflow, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(5)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    SequenceState.of(ctx, "seq_1", "smallint", 8.08),
                    SequenceState.of(ctx, "seq_3", "integer", 8.08),
                    SequenceState.of(ctx, "seq_5", "bigint", 8.08),
                    SequenceState.of(ctx, "seq_full_range_asc", "bigint", 0.0),
                    SequenceState.of(ctx, "seq_full_range_desc", "bigint", 0.0));

            assertThat(check)
                .executing(ctx, SkipBySequenceNamePredicate.ofName(ctx, "seq_1", "seq_3", "seq_5", "seq_full_range_asc", "seq_full_range_desc"))
                .isEmpty();
        });
    }
}
