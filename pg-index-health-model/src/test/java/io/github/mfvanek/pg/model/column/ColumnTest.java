/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnTest {

    @Test
    void gettersShouldWorkForNotNullColumn() {
        final Column column = Column.ofNotNull("t1", "c1", "integer");
        assertThat(column.getTableName())
            .isNotBlank()
            .isEqualTo("t1");
        assertThat(column.getColumnName())
            .isNotBlank()
            .isEqualTo("c1")
            .isEqualTo(column.getName());
        assertThat(column.isNotNull()).isTrue();
        assertThat(column.isNullable()).isFalse();
        assertThat(column.getColumnType())
            .isEqualTo("integer");
        assertThat(column.getObjectType())
            .isEqualTo(PgObjectType.TABLE);
    }

    @Test
    void gettersShouldWorkForNullableColumn() {
        final Column nullableColumn = Column.ofNullable("t2", "c2", PgColumnType.TEXT);
        assertThat(nullableColumn.getTableName())
            .isNotBlank()
            .isEqualTo("t2");
        assertThat(nullableColumn.getColumnName())
            .isNotBlank()
            .isEqualTo("c2")
            .isEqualTo(nullableColumn.getName());
        assertThat(nullableColumn.isNotNull()).isFalse();
        assertThat(nullableColumn.isNullable()).isTrue();
        assertThat(nullableColumn.getColumnType())
            .isEqualTo("text");
        assertThat(nullableColumn.getObjectType())
            .isEqualTo(PgObjectType.TABLE);
    }

    @Test
    void allFactoriesShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(Column.ofNotNull("t1", "c1", PgColumnType.BIGINT))
            .isEqualTo(Column.ofNotNull("t1", "c1", "bigint"))
            .isEqualTo(Column.of("t1", "c1", true, "bigint"));
        assertThat(Column.ofNotNull(ctx, "t1", "c1", PgColumnType.BIGINT))
            .isEqualTo(Column.ofNotNull(ctx, "t1", "c1", "bigint"))
            .isEqualTo(Column.of(ctx, "t1", "c1", true, "bigint"))
            .isEqualTo(Column.of("tst.t1", "c1", true, "bigint"));
        assertThat(Column.ofNullable("t1", "c1", PgColumnType.UUID))
            .isEqualTo(Column.ofNullable("t1", "c1", "uuid"))
            .isEqualTo(Column.of("t1", "c1", false, "uuid"));
        assertThat(Column.ofNullable(ctx, "t1", "c1", PgColumnType.UUID))
            .isEqualTo(Column.ofNullable(ctx, "t1", "c1", "uuid"))
            .isEqualTo(Column.of(ctx, "t1", "c1", false, "uuid"))
            .isEqualTo(Column.of("tst.t1", "c1", false, "uuid"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValuesShouldThrowException() {
        assertThatThrownBy(() -> Column.ofNotNull(null, "c1", "integer"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> Column.ofNotNull("", "c1", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> Column.ofNotNull("   ", "c1", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableName cannot be blank");

        assertThatThrownBy(() -> Column.ofNotNull("t1", null, "integer"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> Column.ofNotNull("t1", "", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> Column.ofNotNull("t1", "   ", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");

        assertThatThrownBy(() -> Column.ofNotNull("t1", "c1", (String) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnType cannot be null");
        assertThatThrownBy(() -> Column.ofNotNull("t1", "c1", (PgColumnType) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnType cannot be null");
        assertThatThrownBy(() -> Column.ofNullable("t1", "c1", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnType cannot be blank");
        assertThatThrownBy(() -> Column.ofNullable("t1", "c1", "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnType cannot be blank");
    }

    @Test
    void testToString() {
        assertThat(Column.ofNotNull("t1", "c1", PgColumnType.INTEGER))
            .hasToString("Column{tableName='t1', columnName='c1', notNull=true, columnType='integer'}");
        assertThat(Column.ofNullable("t2", "c2", PgColumnType.TIMESTAMPTZ))
            .hasToString("Column{tableName='t2', columnName='c2', notNull=false, columnType='timestamp with time zone'}");

        final PgContext ctx = PgContext.of("tst");
        assertThat(Column.ofNotNull(ctx, "t1", "c1", "integer[]"))
            .hasToString("Column{tableName='tst.t1', columnName='c1', notNull=true, columnType='integer[]'}");
        assertThat(Column.ofNullable(ctx, "t2", "c2", PgColumnType.VARCHAR))
            .hasToString("Column{tableName='tst.t2', columnName='c2', notNull=false, columnType='character varying'}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final Column first = Column.ofNotNull("t1", "c1", "integer");
        final Column theSame = Column.ofNotNull("t1", "c1", PgColumnType.INTEGER);
        final Column theSameButNullable = Column.ofNullable("t1", "c1", "integer");
        final Column theSameButAnotherType = Column.ofNotNull("t1", "c1", "bigint");
        final Column second = Column.ofNotNull("t1", "c2", "integer");

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

        // nullability is an attribute, not a part of identity
        assertThat(theSameButNullable)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // type is an attribute, not a part of identity
        assertThat(theSameButAnotherType)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // others
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(Column.class)
            .withIgnoredFields(ColumnNameAware.NOT_NULL_FIELD, ColumnTypeAware.COLUMN_TYPE_FIELD)
            .verify();
    }

    @Test
    void compareToTest() {
        final Column first = Column.ofNotNull("t1", "c1", "integer");
        final Column theSame = Column.ofNotNull("t1", "c1", "integer");
        final Column theSameButNullable = Column.ofNullable("t1", "c1", "integer");
        final Column theSameButAnotherType = Column.ofNotNull("t1", "c1", "bigint");
        final Column second = Column.ofNotNull("t1", "c2", "integer");
        final Column third = Column.ofNotNull("t2", "c1", "integer");

        // noinspection ConstantConditions
        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("other cannot be null");

        assertThat(first)
            .isEqualByComparingTo(first) // self
            .isEqualByComparingTo(theSame) // the same
            .isEqualByComparingTo(theSameButNullable) // nullability is not a part of identity
            .isEqualByComparingTo(theSameButAnotherType) // type is not a part of identity
            .isLessThan(second)
            .isLessThan(third);

        assertThat(second)
            .isGreaterThan(first)
            .isLessThan(third);

        assertThat(third)
            .isGreaterThan(first)
            .isGreaterThan(second);
    }
}
