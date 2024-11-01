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

import java.util.List;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DuplicatedForeignKeysTest {

    @Test
    void objectTypeShouldBeConstraint() {
        final DuplicatedForeignKeys foreignKeys = prepare();
        assertThat(foreignKeys.getObjectType())
            .isEqualTo(PgObjectType.CONSTRAINT);
    }

    @Test
    void testToString() {
        final DuplicatedForeignKeys foreignKeys = prepare();
        assertThat(foreignKeys)
            .hasToString("DuplicatedForeignKeys{tableName='t1', foreignKeys=[" +
                "ForeignKey{tableName='t1', constraintName='c1', columnsInConstraint=[Column{tableName='t1', columnName='col1', notNull=true}]}, " +
                "ForeignKey{tableName='t1', constraintName='c2', columnsInConstraint=[Column{tableName='t1', columnName='col1', notNull=true}]}]}");
    }

    @Test
    void internalCollectionShouldBeUnmodifiable() {
        final DuplicatedForeignKeys foreignKeys = prepare();
        assertThat(foreignKeys.getTableName())
            .isEqualTo("t1");
        assertThat(foreignKeys.getName())
            .isEqualTo("c1,c2");
        assertThat(foreignKeys.getForeignKeys())
            .hasSize(2)
            .isUnmodifiable();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionOnInvalidArguments() {
        assertThatThrownBy(() -> DuplicatedForeignKeys.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("foreignKeys cannot be null");
        final List<ForeignKey> empty = List.of();
        assertThatThrownBy(() -> DuplicatedForeignKeys.of(empty))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rows cannot be empty");
        final List<ForeignKey> single = List.of(ForeignKey.ofNotNullColumn("t1", "c1", "col1"));
        assertThatThrownBy(() -> DuplicatedForeignKeys.of(single))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rows should contains at least two items");
        final List<ForeignKey> twoInvalid = List.of(
            ForeignKey.ofNotNullColumn("t1", "c1", "col1"),
            ForeignKey.ofNotNullColumn("t2", "c2", "col1"));
        assertThatThrownBy(() -> DuplicatedForeignKeys.of(twoInvalid))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name is not the same within given rows");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final DuplicatedForeignKeys first = prepare();
        final DuplicatedForeignKeys theSame = prepare();
        final DuplicatedForeignKeys second = DuplicatedForeignKeys.of(List.of(
            ForeignKey.ofNotNullColumn("t1", "c3", "col2"),
            ForeignKey.ofNotNullColumn("t1", "c4", "col2")));

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        // self
        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(DuplicatedForeignKeys.class)
            .withIgnoredFields("foreignKeysNames")
            .verify();
    }

    @Nonnull
    private DuplicatedForeignKeys prepare() {
        return DuplicatedForeignKeys.of(
            ForeignKey.ofNotNullColumn("t1", "c1", "col1"),
            ForeignKey.ofNotNullColumn("t1", "c2", "col1"));
    }
}
