/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ColumnWithSerialTypeTest {

    @Test
    void gettersShouldWork() {
        final ColumnWithSerialType column = prepare();
        assertThat(column)
                .isNotNull();
        assertThat(column.getTableName())
                .isEqualTo("t1");
        assertThat(column.getColumnName())
                .isEqualTo("c1");
        assertThat(column.isNullable())
                .isFalse();
        assertThat(column.getSerialType())
                .isEqualTo(SerialType.SERIAL);
        assertThat(column.getSequenceName())
                .isEqualTo("s1");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValuesShouldThrowException() {
        assertThatThrownBy(() -> ColumnWithSerialType.of(null, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("column cannot be null");

        final Column column = Column.ofNullable("t1", "c1");
        assertThatThrownBy(() -> ColumnWithSerialType.of(column, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("serialType cannot be null");

        assertThatThrownBy(() -> ColumnWithSerialType.of(column, SerialType.BIG_SERIAL, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("sequenceName cannot be null");

        assertThatThrownBy(() -> ColumnWithSerialType.of(column, SerialType.SMALL_SERIAL, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sequenceName cannot be blank");

        assertThatThrownBy(() -> ColumnWithSerialType.of(column, SerialType.SMALL_SERIAL, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sequenceName cannot be blank");
    }

    @Test
    void toStringTest() {
        assertThat(prepare())
                .hasToString("ColumnWithSerialType{column=Column{tableName='t1', columnName='c1', notNull=true}, serialType=SerialType{pgTypeName='serial'}, sequenceName='s1'}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final ColumnWithSerialType first = prepare();
        final ColumnWithSerialType theSame = prepare();
        final ColumnWithSerialType theSameButNullable = ColumnWithSerialType.of(Column.ofNullable("t1", "c1"), SerialType.SERIAL, "s1");
        final ColumnWithSerialType second = ColumnWithSerialType.of(Column.ofNotNull("t1", "c2"), SerialType.SERIAL, "s1");
        final ColumnWithSerialType third = ColumnWithSerialType.of(Column.ofNotNull("t1", "c1"), SerialType.SMALL_SERIAL, "s1");
        final ColumnWithSerialType forth = ColumnWithSerialType.of(Column.ofNotNull("t1", "c1"), SerialType.SERIAL, "s2");

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(BigDecimal.ZERO)).isFalse();

        // self
        assertThat(first)
                .isEqualTo(first)
                .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
                .isEqualTo(first)
                .hasSameHashCodeAs(first);

        // do not ignore nullability of column
        assertThat(theSameButNullable)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);

        // others
        assertThat(second)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);

        assertThat(forth)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ColumnWithSerialType.class)
                .verify();
    }

    @Test
    void compareToTest() {
        final ColumnWithSerialType first = prepare();
        final ColumnWithSerialType theSame = prepare();
        final ColumnWithSerialType theSameButNullable = ColumnWithSerialType.of(Column.ofNullable("t1", "c1"), SerialType.SERIAL, "s1");
        final ColumnWithSerialType second = ColumnWithSerialType.of(Column.ofNotNull("t1", "c1"), SerialType.SMALL_SERIAL, "s1");
        final ColumnWithSerialType third = ColumnWithSerialType.of(Column.ofNotNull("t1", "c1"), SerialType.BIG_SERIAL, "s1");
        final ColumnWithSerialType forth = ColumnWithSerialType.of(Column.ofNotNull("t1", "c2"), SerialType.SERIAL, "s2");

        //noinspection ResultOfMethodCallIgnored,ConstantConditions
        assertThatThrownBy(() -> first.compareTo(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("other cannot be null");

        assertThat(first)
                .isEqualByComparingTo(first) // self
                .isEqualByComparingTo(theSame) // the same
                .isGreaterThan(theSameButNullable) // do not ignore nullability of column
                .isGreaterThan(second)
                .isLessThan(third)
                .isLessThan(forth);

        assertThat(theSameButNullable)
                .isLessThan(first);

        assertThat(second)
                .isLessThan(first)
                .isLessThan(third)
                .isLessThan(forth);

        assertThat(third)
                .isGreaterThan(first)
                .isGreaterThan(second)
                .isLessThan(forth);
    }

    @Nonnull
    private static ColumnWithSerialType prepare() {
        return ColumnWithSerialType.of(Column.ofNotNull("t1", "c1"), SerialType.SERIAL, "s1");
    }
}
