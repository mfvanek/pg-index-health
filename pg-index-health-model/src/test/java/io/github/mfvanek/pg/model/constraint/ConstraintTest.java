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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstraintTest {

    @Test
    void testToString() {
        final Constraint constraintWithCheck = Constraint.of("t", "not_valid_id", ConstraintType.CHECK);
        assertThat(constraintWithCheck)
                .hasToString("Constraint{tableName='t', constraintName='not_valid_id', constraintType=CHECK}");

        final Constraint constraintWithForeignKey = Constraint.of("t", "not_valid_id", ConstraintType.FOREIGN_KEY);
        assertThat(constraintWithForeignKey)
                .hasToString("Constraint{tableName='t', constraintName='not_valid_id', constraintType=FOREIGN_KEY}");
    }

    @Test
    void constraint() {
        final Constraint constraintWithCheck = Constraint.of("t", "not_valid_id", ConstraintType.CHECK);
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

        final Constraint constraintWithForeignKey = Constraint.of("t", "not_valid_id", ConstraintType.FOREIGN_KEY);
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

    @Test
    void equalsAndHashCode() {
        final Constraint first = new Constraint("t", "not_valid_id", ConstraintType.CHECK);
        final Constraint theSame = new Constraint("t", "not_valid_id", ConstraintType.CHECK);
        final Constraint different = new Constraint("t", "valid_id", ConstraintType.CHECK);
        final Constraint second = new Constraint("t", "not_valid_id", ConstraintType.FOREIGN_KEY);

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

        assertThat(second)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);
    }
}
