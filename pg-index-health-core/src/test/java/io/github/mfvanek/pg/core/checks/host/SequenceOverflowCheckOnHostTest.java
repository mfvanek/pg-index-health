/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipBySequenceNamePredicate;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class SequenceOverflowCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<SequenceState> check = new SequenceOverflowCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(SequenceState.class)
            .hasDiagnostic(Diagnostic.SEQUENCE_OVERFLOW)
            .hasHost(getHost())
            .isRuntime();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithSequences(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSequenceOverflow, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                    SequenceState.of(ctx.enrichWithSchema("seq_1"), "smallint", 8.08),
                    SequenceState.of(ctx.enrichWithSchema("seq_3"), "integer", 8.08),
                    SequenceState.of(ctx.enrichWithSchema("seq_5"), "bigint", 8.08));

            assertThat(check)
                .executing(ctx, SkipBySequenceNamePredicate.of(ctx, List.of("seq_1", "seq_3", "seq_5")))
                .isEmpty();
        });
    }
}
