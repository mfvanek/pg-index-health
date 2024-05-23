/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class SequenceOverflowCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<SequenceState> check = new SequenceOverflowCheckHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(SequenceState.class)
            .hasDiagnostic(Diagnostic.SEQUENCE_OVERFLOW)
            .hasHost(getHost());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithSequences(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSequenceOverflow, ctx -> {
            final List<SequenceState> actual = check.check(ctx);

            final List<SequenceState> expected = List.of(
                SequenceState.of("accounts_seq", "bigint", 100.0),
                SequenceState.of("clients_seq", "bigint", 100.0),
                SequenceState.of("seq_1", "smallint", 8.08),
                SequenceState.of("seq_3", "integer", 99.1),
                SequenceState.of("seq_5", "bigint", 50.01)
            );

            Assertions.assertThat(actual)
                .hasSize(expected.size())
                .allMatch(state -> expected.stream().anyMatch(
                    exp -> state.getSequenceName().endsWith(exp.getSequenceName()) &&
                        state.getDataType().equals(exp.getDataType()) &&
                        state.getRemainingPercentage() == exp.getRemainingPercentage())
                );
        });
    }
}
