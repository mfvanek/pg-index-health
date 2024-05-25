/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.sequence;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SequenceStateTest {

    @Test
    void testToString() {
        final SequenceState sequenceState = SequenceState.of("accounts_seq", "bigint", 100.0);
        assertThat(sequenceState)
            .hasToString("SequenceState{sequenceName='accounts_seq', dataType='bigint', remainingPercentage=100.0}");
    }

    @Test
    void sequenceState() {
        final SequenceState sequenceState = SequenceState.of("accounts_seq", "bigint", 100.0);
        assertThat(sequenceState.getSequenceName())
            .isNotBlank()
            .isEqualTo("accounts_seq");
        assertThat(sequenceState.getDataType())
            .isNotBlank()
            .isEqualTo("bigint");
        assertThat(sequenceState.getName())
            .isNotBlank()
            .isEqualTo("accounts_seq");
        assertThat(sequenceState.getRemainingPercentage())
            .isEqualTo(100.0);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> SequenceState.of(null, null, 100.0))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("sequenceName cannot be null");
        assertThatThrownBy(() -> SequenceState.of("accounts_seq", null, 100.0))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("dataType cannot be null");
        assertThatThrownBy(() -> SequenceState.of("", "bigint", 100.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sequenceName cannot be blank");
        assertThatThrownBy(() -> SequenceState.of("accounts_seq", "", 100.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("dataType cannot be blank");
        assertThatThrownBy(() -> SequenceState.of("accounts_seq", "bigint", -1.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("remainingPercentage should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> SequenceState.of("accounts_seq", "bigint", 101.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("remainingPercentage should be in the range from 0.0 to 100.0 inclusive");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final SequenceState first = SequenceState.of("accounts_seq", "bigint", 100.0);
        final SequenceState theSame = SequenceState.of("accounts_seq", "bigint", 100.0);
        final SequenceState different = SequenceState.of("clients_seq", "bigint", 100.0);

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        assertThat(theSame)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        assertThat(different)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void gettersShouldReturnCorrectValues() {
        final SequenceState sequenceState = SequenceState.of("clients_seq", "integer", 50.0);
        assertThat(sequenceState.getSequenceName()).isEqualTo("clients_seq");
        assertThat(sequenceState.getDataType()).isEqualTo("integer");
        assertThat(sequenceState.getRemainingPercentage()).isEqualTo(50.0);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(SequenceState.class)
            .withIgnoredFields("dataType")
            .withIgnoredFields("remainingPercentage")
            .verify();
    }
}
