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
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnTest {

    @Test
    void gettersShouldWork() {
        final Column column = Column.ofNotNull("t1", "c1");
        assertThat(column.getTableName())
                .isNotBlank()
                .isEqualTo("t1");
        assertThat(column.getColumnName())
                .isNotBlank()
                .isEqualTo("c1");
        assertThat(column.isNotNull()).isTrue();
        assertThat(column.isNullable()).isFalse();

        final Column nullableColumn = Column.ofNullable("t2", "c2");
        assertThat(nullableColumn.getTableName())
                .isNotBlank()
                .isEqualTo("t2");
        assertThat(nullableColumn.getColumnName())
                .isNotBlank()
                .isEqualTo("c2");
        assertThat(nullableColumn.isNotNull()).isFalse();
        assertThat(nullableColumn.isNullable()).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValuesShouldThrowException() {
        assertThatThrownBy(() -> Column.ofNotNull(null, "c1"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> Column.ofNotNull("", "c1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> Column.ofNotNull("   ", "c1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tableName cannot be blank");

        assertThatThrownBy(() -> Column.ofNotNull("t1", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> Column.ofNotNull("t1", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> Column.ofNotNull("t1", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("columnName cannot be blank");
    }

    @Test
    void testToString() {
        final Column column = Column.ofNotNull("t1", "c1");
        assertThat(column).hasToString("Column{tableName='t1', columnName='c1', notNull=true}");

        final Column nullableColumn = Column.ofNullable("t2", "c2");
        assertThat(nullableColumn).hasToString("Column{tableName='t2', columnName='c2', notNull=false}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final Column first = Column.ofNotNull("t1", "c1");
        final Column theSame = Column.ofNotNull("t1", "c1");
        final Column theSameButNullable = Column.ofNullable("t1", "c1");
        final Column second = Column.ofNotNull("t1", "c2");

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(BigDecimal.ZERO)).isFalse();

        // self
        assertThat(first).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(first.hashCode());

        // the same
        assertThat(theSame).isEqualTo(first);
        assertThat(theSame.hashCode()).isEqualTo(first.hashCode());

        // do not ignore nullability of column
        assertThat(theSameButNullable).isNotEqualTo(first);
        assertThat(theSameButNullable.hashCode()).isNotEqualTo(first.hashCode());

        // others
        assertThat(second).isNotEqualTo(first);
        assertThat(first).isNotEqualTo(second);
        assertThat(second.hashCode()).isNotEqualTo(first.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(Column.class)
                .verify();
    }

    @Test
    void compareToTest() {
        final Column first = Column.ofNotNull("t1", "c1");
        final Column theSame = Column.ofNotNull("t1", "c1");
        final Column theSameButNullable = Column.ofNullable("t1", "c1");
        final Column second = Column.ofNotNull("t1", "c2");
        final Column third = Column.ofNotNull("t2", "c1");

        //noinspection ResultOfMethodCallIgnored,ConstantConditions
        assertThatThrownBy(() -> first.compareTo(null))
                .isInstanceOf(NullPointerException.class);

        // self
        //noinspection EqualsWithItself
        assertThat(first.compareTo(first)).isZero();

        // the same
        assertThat(first.compareTo(theSame)).isZero();

        // do not ignore nullability of column
        assertThat(theSameButNullable.compareTo(first)).isEqualTo(-1);
        assertThat(first.compareTo(theSameButNullable)).isEqualTo(1);

        // others
        assertThat(first.compareTo(second)).isEqualTo(-1);
        assertThat(second.compareTo(first)).isEqualTo(1);

        assertThat(first.compareTo(third)).isEqualTo(-1);
        assertThat(third.compareTo(first)).isEqualTo(1);
    }
}
