/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.column.PgColumnType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ForeignKeyTest {

    @Test
    void testToString() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(ForeignKey.ofNotNull(ctx, "t", "c_t_order_id", "order_id", "integer"))
            .hasToString("ForeignKey{tableName='tst.t', constraintName='c_t_order_id', columns=[Column{tableName='tst.t', columnName='order_id', notNull=true, columnType='integer'}]}");

        assertThat(ForeignKey.ofNullable(ctx, "t", "c_t_order_id", "order_id", "integer"))
            .hasToString("ForeignKey{tableName='tst.t', constraintName='c_t_order_id', columns=[Column{tableName='tst.t', columnName='order_id', notNull=false, columnType='integer'}]}");
    }

    @Test
    void foreignKey() {
        final PgContext ctx = PgContext.ofDefault();
        final ForeignKey foreignKey = ForeignKey.ofInteger(ctx, "t", "c_t_order_id", "order_id");
        assertThat(foreignKey.getTableName())
            .isNotBlank()
            .isEqualTo("t");
        assertThat(foreignKey.getConstraintName())
            .isNotBlank()
            .isEqualTo("c_t_order_id")
            .isEqualTo(foreignKey.getName());
        assertThat(foreignKey.getColumns())
            .hasSize(1)
            .containsExactly(Column.ofInteger(ctx, "t", "order_id"))
            .isUnmodifiable();
        assertThat(foreignKey.getObjectType())
            .isEqualTo(PgObjectType.CONSTRAINT);
        assertThat(foreignKey.getConstraintType())
            .isEqualTo(ConstraintType.FOREIGN_KEY);
        assertThat(foreignKey.toConstraint())
            .isEqualTo(Constraint.ofType("t", "c_t_order_id", ConstraintType.FOREIGN_KEY));
    }

    @Test
    void getColumns() {
        final ForeignKey first = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.of("t", "order_id", true, "integer"), Column.of("t", "item_id", true, "integer")));
        assertThat(first.getColumns())
            .hasSize(2)
            .containsExactly(Column.of("t", "order_id", true, "integer"), Column.of("t", "item_id", true, "integer"))
            .isUnmodifiable();

        final PgContext ctx = PgContext.of("tst");
        final ForeignKey second = ForeignKey.of(ctx, "t", "c_t_order_id",
            List.of(Column.ofInteger(ctx, "t", "order_id"), Column.ofInteger(ctx, "t", "item_id")));
        assertThat(second.getColumns())
            .hasSize(2)
            .containsExactly(Column.of("tst.t", "order_id", true, "integer"), Column.of("tst.t", "item_id", true, "integer"))
            .isUnmodifiable();
        assertThat(second.getTableName())
            .isEqualTo("tst.t");
    }

    @Test
    void shouldCreateDefensiveCopyOfColumnsList() {
        final PgContext ctx = PgContext.ofDefault();
        final List<Column> columns = new ArrayList<>(List.of(
            Column.ofInteger(ctx, "t", "first"),
            Column.ofInteger(ctx, "t", "second"),
            Column.ofInteger(ctx, "t", "third")));
        final ForeignKey key = ForeignKey.of("t", "c_t_fk", columns);

        columns.add(Column.ofInteger(ctx, "t", "fourth"));

        assertThat(key.getColumns())
            .hasSize(3)
            .doesNotContain(Column.ofInteger(ctx, "t", "fourth"))
            .isUnmodifiable();
    }

    @Test
    void allColumnMustBelongToTheSameTable() {
        final PgContext ctx = PgContext.ofDefault();
        final List<Column> columns = List.of(
            Column.ofInteger(ctx, "t", "first"),
            Column.ofInteger(ctx, "t1", "second"));
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_fk", columns))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name is not the same within given rows");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> ForeignKey.of(null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> ForeignKey.of("t", null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("constraintName cannot be null");
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_order_id", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columns cannot be null");
        final List<Column> columns = List.of();
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_order_id", columns))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columns cannot be empty");
        assertThatThrownBy(() -> ForeignKey.ofColumn(ctx, "t", "fk", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNotNull(ctx, "t", "fk", null, "integer"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNotNull(ctx, "t", "fk", "", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNotNull(ctx, "t", "fk", "  ", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNullable(ctx, "t", "fk", null, "integer"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNullable(ctx, "t", "fk", "", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNullable(ctx, "t", "fk", "  ", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        final Column column = Column.ofInteger(ctx, "t", "col");
        final Constraint constraint = Constraint.ofType("t", "c", ConstraintType.CHECK);
        assertThatThrownBy(() -> ForeignKey.ofColumn(constraint, column))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("constraint must be foreign key");
        assertThatThrownBy(() -> ForeignKey.ofColumn(null, column))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("constraint cannot be null");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final PgContext ctx = PgContext.ofDefault();
        final ForeignKey first = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofInteger(ctx, "t", "order_id"), Column.ofInteger(ctx, "t", "limit")));
        final ForeignKey theSame = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofInteger(ctx, "t", "order_id"), Column.ofInteger(ctx, "t", "limit")));
        final ForeignKey withDifferentOrderOfColumns = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofInteger(ctx, "t", "limit"), Column.ofInteger(ctx, "t", "order_id")));
        final ForeignKey withDifferentColumnName = ForeignKey.ofNullableInteger(ctx, "t", "c_t_order_id", "no_matter_what");

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

        // column order doesn't matter
        assertThat(withDifferentOrderOfColumns)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the column name doesn't matter
        assertThat(withDifferentColumnName)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        final ForeignKey third = ForeignKey.of("table", "c_t_order_id",
            List.of(Column.ofInteger(ctx, "table", "order_id"), Column.ofInteger(ctx, "table", "limit")));
        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        final ForeignKey fourth = ForeignKey.of("t", "other_id",
            List.of(Column.ofInteger(ctx, "t", "order_id"), Column.ofInteger(ctx, "t", "limit")));
        assertThat(fourth)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void factoriesWithEnumTypeShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(ForeignKey.ofNotNull(ctx, "t", "c_t_order_id", "order_id", PgColumnType.BIGINT))
            .usingRecursiveComparison()
            .isEqualTo(ForeignKey.ofNotNull(ctx, "t", "c_t_order_id", "order_id", "bigint"));
        assertThat(ForeignKey.ofNullable(ctx, "t", "c_t_order_id", "order_id", PgColumnType.UUID))
            .usingRecursiveComparison()
            .isEqualTo(ForeignKey.ofNullable(ctx, "t", "c_t_order_id", "order_id", "uuid"));
    }

    @Test
    void typedFactoriesShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(List.of(
            ForeignKey.ofBigint(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableBigint(ctx, "t1", "fk", "c1"),
            ForeignKey.ofInteger(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableInteger(ctx, "t1", "fk", "c1"),
            ForeignKey.ofVarchar(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableVarchar(ctx, "t1", "fk", "c1"),
            ForeignKey.ofText(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableText(ctx, "t1", "fk", "c1"),
            ForeignKey.ofCharacter(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableCharacter(ctx, "t1", "fk", "c1"),
            ForeignKey.ofBoolean(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableBoolean(ctx, "t1", "fk", "c1"),
            ForeignKey.ofTimestamp(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableTimestamp(ctx, "t1", "fk", "c1"),
            ForeignKey.ofTimestamptz(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableTimestamptz(ctx, "t1", "fk", "c1"),
            ForeignKey.ofUuid(ctx, "t1", "fk", "c1"),
            ForeignKey.ofNullableUuid(ctx, "t1", "fk", "c1")))
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "bigint"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "bigint"),
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "integer"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "integer"),
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "character varying"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "character varying"),
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "text"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "text"),
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "character"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "character"),
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "boolean"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "boolean"),
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "timestamp without time zone"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "timestamp without time zone"),
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "timestamp with time zone"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "timestamp with time zone"),
                ForeignKey.ofNotNull(ctx, "t1", "fk", "c1", "uuid"),
                ForeignKey.ofNullable(ctx, "t1", "fk", "c1", "uuid"));
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ForeignKey.class)
            .withIgnoredFields(ColumnsAware.COLUMNS_FIELD)
            .verify();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void compareToTest() {
        final PgContext ctx = PgContext.ofDefault();
        final ForeignKey first = ForeignKey.ofInteger(ctx, "t1", "c1", "col1");
        final ForeignKey theSame = ForeignKey.ofInteger(ctx, "t1", "c1", "col2"); // different column!
        final ForeignKey secondName = ForeignKey.ofInteger(ctx, "t1", "c2", "col1");
        final ForeignKey secondTable = ForeignKey.ofInteger(ctx, "t2", "c1", "col1");

        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("other cannot be null");

        assertThat(first)
            .isEqualByComparingTo(first)
            .isEqualByComparingTo(theSame)
            .isLessThan(secondName)
            .isLessThan(secondTable);

        assertThat(secondName)
            .isGreaterThan(first)
            .isLessThan(secondTable);

        assertThat(secondTable)
            .isGreaterThan(first)
            .isGreaterThan(secondName);
    }
}
