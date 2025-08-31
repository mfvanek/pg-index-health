/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnWithTypeTest {

    @Test
    void gettersShouldWork() {
        final ColumnWithType column = prepare();
        assertThat(column)
            .isNotNull()
            .satisfies(c -> {
                assertThat(c.getTableName())
                    .isEqualTo("t1");
                assertThat(c.getColumnName())
                    .isEqualTo("c1")
                    .isEqualTo(c.getName());
                assertThat(c.isNullable())
                    .isFalse();
                assertThat(c.isNotNull())
                    .isTrue();
                assertThat(c.getColumnType())
                    .isEqualTo("text");
                assertThat(c.getObjectType())
                    .isEqualTo(PgObjectType.TABLE);
                assertThat(c.toColumn())
                    .isEqualTo(Column.ofNotNull("t1", "c1"));
            });

        final ColumnWithType theSameButNullable = prepareNullable();
        assertThat(theSameButNullable)
            .isNotNull()
            .satisfies(c -> {
                assertThat(c.getTableName())
                    .isEqualTo("t1");
                assertThat(c.getColumnName())
                    .isEqualTo("c1")
                    .isEqualTo(c.getName());
                assertThat(c.isNullable())
                    .isTrue();
                assertThat(c.isNotNull())
                    .isFalse();
                assertThat(c.getColumnType())
                    .isEqualTo("text");
                assertThat(c.toColumn())
                    .isEqualTo(Column.ofNullable("t1", "c1"));
            });
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValuesShouldThrowException() {
        assertThatThrownBy(() -> ColumnWithType.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");

        final Column column = Column.ofNullable("t1", "c1");
        assertThatThrownBy(() -> ColumnWithType.of(column, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnType cannot be null");

        assertThatThrownBy(() -> ColumnWithType.of(column, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnType cannot be blank");

        assertThatThrownBy(() -> ColumnWithType.of(column, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnType cannot be blank");
    }

    @Test
    void toStringTest() {
        assertThat(prepare())
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=true}, columnType='text'}");

        assertThat(prepareNullable())
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=false}, columnType='text'}");

        final Column column = Column.ofNotNull("t1", "c1");
        assertThat(ColumnWithType.ofVarchar(column))
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=true}, columnType='character varying'}");

        assertThat(ColumnWithType.ofBigint(column))
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=true}, columnType='bigint'}");

        assertThat(ColumnWithType.ofInteger(column))
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=true}, columnType='integer'}");

        assertThat(ColumnWithType.ofSmallint(column))
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=true}, columnType='smallint'}");

        assertThat(ColumnWithType.ofTimestamp(column))
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=true}, columnType='timestamp without time zone'}");

        assertThat(ColumnWithType.ofTimestamptz(column))
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=true}, columnType='timestamp with time zone'}");

        assertThat(ColumnWithType.ofUuid(column))
            .hasToString("ColumnWithType{column=Column{tableName='t1', columnName='c1', notNull=true}, columnType='uuid'}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final ColumnWithType first = prepare();
        final ColumnWithType theSame = prepare();
        final ColumnWithType theSameButNullable = prepareNullable();
        final ColumnWithType second = ColumnWithType.ofBigint(Column.ofNotNull("t1", "c1"));

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

        // do not ignore nullability of column
        assertThat(theSameButNullable)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        // ignores a column type
        assertThat(second)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ColumnWithType.class)
            .withIgnoredFields(ColumnTypeAware.COLUMN_TYPE_FIELD)
            .verify();
    }

    @Test
    void compareToTest() {
        final ColumnWithType first = prepare();
        final ColumnWithType theSame = prepare();
        final ColumnWithType theSameButNullable = prepareNullable();
        final ColumnWithType second = ColumnWithType.ofBigint(Column.ofNotNull("t1", "c1"));
        final ColumnWithType third = ColumnWithType.ofUuid(Column.ofNotNull("t1", "c1"));

        // noinspection ConstantConditions
        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("other cannot be null");

        assertThat(first)
            .isEqualByComparingTo(first) // self
            .isEqualByComparingTo(theSame) // the same
            .isGreaterThan(theSameButNullable) // do not ignore nullability of column
            .isGreaterThan(second)
            .isLessThan(third);

        assertThat(theSameButNullable)
            .isLessThan(first);

        assertThat(second)
            .isLessThan(first)
            .isLessThan(third);
    }

    @NonNull
    private static ColumnWithType prepare() {
        return ColumnWithType.ofText(Column.ofNotNull("t1", "c1"));
    }

    @NonNull
    private static ColumnWithType prepareNullable() {
        return ColumnWithType.ofText(Column.ofNullable("t1", "c1"));
    }
}
