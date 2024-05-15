/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConstraintTest {

    @Test
    void testToString() {
        final Constraint constraintWithCheck = Constraint.ofType("t", "not_valid_id", ConstraintType.CHECK);
        assertThat(constraintWithCheck)
            .hasToString("Constraint{tableName='t', constraintName='not_valid_id', constraintType=CHECK}");

        final Constraint constraintWithForeignKey = Constraint.ofType("t", "not_valid_id", ConstraintType.FOREIGN_KEY);
        assertThat(constraintWithForeignKey)
            .hasToString("Constraint{tableName='t', constraintName='not_valid_id', constraintType=FOREIGN_KEY}");
    }

    @Test
    void constraint() {
        final Constraint constraintWithCheck = Constraint.ofType("t", "not_valid_id", ConstraintType.CHECK);
        assertThat(constraintWithCheck.getTableName())
            .isNotBlank()
            .isEqualTo("t");
        assertThat(constraintWithCheck.getConstraintName())
            .isNotBlank()
            .isEqualTo("not_valid_id")
            .isEqualTo(constraintWithCheck.getName());
        assertThat(constraintWithCheck.getConstraintType())
            .isNotNull()
            .isEqualTo(ConstraintType.CHECK);

        final Constraint constraintWithForeignKey = Constraint.ofType("t", "not_valid_id", ConstraintType.FOREIGN_KEY);
        assertThat(constraintWithForeignKey.getTableName())
            .isNotBlank()
            .isEqualTo("t");
        assertThat(constraintWithForeignKey.getConstraintName())
            .isNotBlank()
            .isEqualTo("not_valid_id")
            .isEqualTo(constraintWithForeignKey.getName());
        assertThat(constraintWithForeignKey.getConstraintType())
            .isNotNull()
            .isEqualTo(ConstraintType.FOREIGN_KEY);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> Constraint.ofType(null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> Constraint.ofType("t", null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("constraintName cannot be null");
        assertThatThrownBy(() -> Constraint.ofType("t", "c_t_order_id", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("constraintType cannot be null");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final Constraint first = new Constraint("t", "not_valid_id", ConstraintType.CHECK);
        final Constraint theSame = new Constraint("t", "not_valid_id", ConstraintType.CHECK);
        final Constraint different = new Constraint("t", "valid_id", ConstraintType.CHECK);
        final Constraint constraintTypeDoesntMatter = new Constraint("t", "not_valid_id", ConstraintType.FOREIGN_KEY);
        final Constraint third = new Constraint("t1", "not_valid_id", ConstraintType.FOREIGN_KEY);

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

        assertThat(constraintTypeDoesntMatter)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first)
            .isNotEqualTo(constraintTypeDoesntMatter)
            .doesNotHaveSameHashCodeAs(constraintTypeDoesntMatter);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(Constraint.class)
            .withIgnoredFields("constraintType")
            .verify();
    }
}
