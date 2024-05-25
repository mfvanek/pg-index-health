/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SequenceOverflowCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<SequenceState> check = new SequenceOverflowCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(SequenceState.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.SEQUENCE_OVERFLOW);
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithSequences(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSequenceOverflow, ctx -> {
            final List<SequenceState> actual = check.check(ctx);
            assertThat(actual)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                    SequenceState.of(ctx.enrichWithSchema("seq_1"), "smallint", 8.08),
                    SequenceState.of(ctx.enrichWithSchema("seq_3"), "integer", 8.08),
                    SequenceState.of(ctx.enrichWithSchema("seq_5"), "bigint", 8.08));
        });
    }
}
