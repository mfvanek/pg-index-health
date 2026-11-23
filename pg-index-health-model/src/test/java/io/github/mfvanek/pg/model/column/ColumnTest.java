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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnTest {

    @Test
    void gettersShouldWorkForNotNullColumn() {
        final PgContext ctx = PgContext.ofDefault();
        final Column column = Column.ofInteger(ctx, "t1", "c1");
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
        final PgContext ctx = PgContext.ofDefault();
        final Column nullableColumn = Column.ofNullableText(ctx, "t2", "c2");
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
        assertThat(Column.ofNotNull(ctx, "t1", "c1", PgColumnType.BIGINT))
            .isEqualTo(Column.ofNotNull(ctx, "t1", "c1", "bigint"))
            .isEqualTo(Column.of(ctx, "t1", "c1", true, "bigint"))
            .isEqualTo(Column.of("tst.t1", "c1", true, "bigint"));
        assertThat(Column.ofNullable(ctx, "t1", "c1", PgColumnType.UUID))
            .isEqualTo(Column.ofNullable(ctx, "t1", "c1", "uuid"))
            .isEqualTo(Column.of(ctx, "t1", "c1", false, "uuid"))
            .isEqualTo(Column.of("tst.t1", "c1", false, "uuid"));
    }

    @Test
    void typedFactoriesShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(List.of(
            Column.ofBigint(ctx, "t1", "c1"),
            Column.ofNullableBigint(ctx, "t1", "c1"),
            Column.ofInteger(ctx, "t1", "c1"),
            Column.ofNullableInteger(ctx, "t1", "c1"),
            Column.ofVarchar(ctx, "t1", "c1"),
            Column.ofNullableVarchar(ctx, "t1", "c1"),
            Column.ofText(ctx, "t1", "c1"),
            Column.ofNullableText(ctx, "t1", "c1"),
            Column.ofCharacter(ctx, "t1", "c1"),
            Column.ofNullableCharacter(ctx, "t1", "c1"),
            Column.ofBoolean(ctx, "t1", "c1"),
            Column.ofNullableBoolean(ctx, "t1", "c1"),
            Column.ofTimestamp(ctx, "t1", "c1"),
            Column.ofNullableTimestamp(ctx, "t1", "c1"),
            Column.ofTimestamptz(ctx, "t1", "c1"),
            Column.ofNullableTimestamptz(ctx, "t1", "c1"),
            Column.ofUuid(ctx, "t1", "c1"),
            Column.ofNullableUuid(ctx, "t1", "c1")))
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                Column.of("tst.t1", "c1", true, "bigint"),
                Column.of("tst.t1", "c1", false, "bigint"),
                Column.of("tst.t1", "c1", true, "integer"),
                Column.of("tst.t1", "c1", false, "integer"),
                Column.of("tst.t1", "c1", true, "character varying"),
                Column.of("tst.t1", "c1", false, "character varying"),
                Column.of("tst.t1", "c1", true, "text"),
                Column.of("tst.t1", "c1", false, "text"),
                Column.of("tst.t1", "c1", true, "character"),
                Column.of("tst.t1", "c1", false, "character"),
                Column.of("tst.t1", "c1", true, "boolean"),
                Column.of("tst.t1", "c1", false, "boolean"),
                Column.of("tst.t1", "c1", true, "timestamp without time zone"),
                Column.of("tst.t1", "c1", false, "timestamp without time zone"),
                Column.of("tst.t1", "c1", true, "timestamp with time zone"),
                Column.of("tst.t1", "c1", false, "timestamp with time zone"),
                Column.of("tst.t1", "c1", true, "uuid"),
                Column.of("tst.t1", "c1", false, "uuid"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValuesShouldThrowException() {
        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> Column.of(null, "c1", true, "integer"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> Column.of("", "c1", true, "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> Column.of("   ", "c1", true, "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableName cannot be blank");

        assertThatThrownBy(() -> Column.of("t1", null, true, "integer"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> Column.of("t1", "", true, "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> Column.of("t1", "   ", true, "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");

        assertThatThrownBy(() -> Column.of("t1", "c1", true, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnType cannot be null");
        assertThatThrownBy(() -> Column.ofNotNull(ctx, "t1", "c1", (PgColumnType) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnType cannot be null");
        assertThatThrownBy(() -> Column.of("t1", "c1", false, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnType cannot be blank");
        assertThatThrownBy(() -> Column.of("t1", "c1", false, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnType cannot be blank");
    }

    @Test
    void testToString() {
        assertThat(Column.of("t1", "c1", true, "integer"))
            .hasToString("Column{tableName='t1', columnName='c1', notNull=true, columnType='integer'}");
        assertThat(Column.of("t2", "c2", false, "timestamp with time zone"))
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
        final PgContext ctx = PgContext.ofDefault();
        final Column first = Column.ofInteger(ctx, "t1", "c1");
        final Column theSame = Column.ofInteger(ctx, "t1", "c1");
        final Column theSameButNullable = Column.ofNullableInteger(ctx, "t1", "c1");
        final Column theSameButAnotherType = Column.ofBigint(ctx, "t1", "c1");
        final Column second = Column.ofInteger(ctx, "t1", "c2");

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
            .withIgnoredFields(ColumnNameAware.NOT_NULL_FIELD, ColumnNameAware.COLUMN_TYPE_FIELD)
            .verify();
    }

    @Test
    void compareToTest() {
        final PgContext ctx = PgContext.ofDefault();
        final Column first = Column.ofInteger(ctx, "t1", "c1");
        final Column theSame = Column.ofInteger(ctx, "t1", "c1");
        final Column theSameButNullable = Column.ofNullableInteger(ctx, "t1", "c1");
        final Column theSameButAnotherType = Column.ofBigint(ctx, "t1", "c1");
        final Column second = Column.ofInteger(ctx, "t1", "c2");
        final Column third = Column.ofInteger(ctx, "t2", "c1");

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
