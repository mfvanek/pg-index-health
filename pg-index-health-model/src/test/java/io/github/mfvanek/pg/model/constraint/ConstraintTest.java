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

import io.github.mfvanek.pg.model.object.PgObjectType;
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
    void gettersShouldWorkForCheckConstraint() {
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
        assertThat(constraintWithCheck.getObjectType())
            .isEqualTo(PgObjectType.CONSTRAINT);
    }

    @Test
    void gettersShouldWorkForForeignKeyConstraint() {
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
        assertThat(constraintWithForeignKey.getObjectType())
            .isEqualTo(PgObjectType.CONSTRAINT);
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
        final Constraint first = Constraint.ofType("t", "not_valid_id", ConstraintType.CHECK);
        final Constraint theSame = Constraint.ofType("t", "not_valid_id", ConstraintType.CHECK);
        final Constraint different = Constraint.ofType("t", "valid_id", ConstraintType.CHECK);
        final Constraint constraintTypeDoesntMatter = Constraint.ofType("t", "not_valid_id", ConstraintType.FOREIGN_KEY);
        final Constraint third = Constraint.ofType("t1", "not_valid_id", ConstraintType.FOREIGN_KEY);

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

    @Test
    void getValidateSqlShouldWork() {
        assertThat(Constraint.ofType("t", "not_valid_id", ConstraintType.CHECK).getValidateSql())
            .isEqualTo("alter table t validate constraint not_valid_id;");
        assertThat(Constraint.ofType("custom_schema.t", "not_valid_id", ConstraintType.CHECK).getValidateSql())
            .isEqualTo("alter table custom_schema.t validate constraint not_valid_id;");
    }
}
