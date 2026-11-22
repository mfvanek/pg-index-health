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
        assertThat(ForeignKey.ofNotNullColumn("t", "c_t_order_id", "order_id", "integer"))
            .hasToString("ForeignKey{tableName='t', constraintName='c_t_order_id', columns=[Column{tableName='t', columnName='order_id', notNull=true, columnType='integer'}]}");

        final PgContext ctx = PgContext.of("tst");
        assertThat(ForeignKey.ofNotNullColumn(ctx, "t", "c_t_order_id", "order_id", "integer"))
            .hasToString("ForeignKey{tableName='tst.t', constraintName='c_t_order_id', columns=[Column{tableName='tst.t', columnName='order_id', notNull=true, columnType='integer'}]}");

        assertThat(ForeignKey.ofNullableColumn("t", "c_t_order_id", "order_id", "integer"))
            .hasToString("ForeignKey{tableName='t', constraintName='c_t_order_id', columns=[Column{tableName='t', columnName='order_id', notNull=false, columnType='integer'}]}");

        assertThat(ForeignKey.ofNullableColumn(ctx, "t", "c_t_order_id", "order_id", "integer"))
            .hasToString("ForeignKey{tableName='tst.t', constraintName='c_t_order_id', columns=[Column{tableName='tst.t', columnName='order_id', notNull=false, columnType='integer'}]}");
    }

    @Test
    void foreignKey() {
        final ForeignKey foreignKey = ForeignKey.ofNotNullColumn("t", "c_t_order_id", "order_id", "integer");
        assertThat(foreignKey.getTableName())
            .isNotBlank()
            .isEqualTo("t");
        assertThat(foreignKey.getConstraintName())
            .isNotBlank()
            .isEqualTo("c_t_order_id")
            .isEqualTo(foreignKey.getName());
        assertThat(foreignKey.getColumns())
            .hasSize(1)
            .containsExactly(Column.ofNotNull("t", "order_id", "integer"))
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
            List.of(Column.ofNotNull("t", "order_id", "integer"), Column.ofNotNull("t", "item_id", "integer")));
        assertThat(first.getColumns())
            .hasSize(2)
            .containsExactly(Column.ofNotNull("t", "order_id", "integer"), Column.ofNotNull("t", "item_id", "integer"))
            .isUnmodifiable();

        final PgContext ctx = PgContext.of("tst");
        final ForeignKey second = ForeignKey.of(ctx, "t", "c_t_order_id",
            List.of(Column.ofNotNull(ctx, "t", "order_id", "integer"), Column.ofNotNull(ctx, "t", "item_id", "integer")));
        assertThat(second.getColumns())
            .hasSize(2)
            .containsExactly(Column.ofNotNull("tst.t", "order_id", "integer"), Column.ofNotNull("tst.t", "item_id", "integer"))
            .isUnmodifiable();
        assertThat(second.getTableName())
            .isEqualTo("tst.t");
    }

    @Test
    void shouldCreateDefensiveCopyOfColumnsList() {
        final List<Column> columns = new ArrayList<>(List.of(
            Column.ofNotNull("t", "first", "integer"),
            Column.ofNotNull("t", "second", "integer"),
            Column.ofNotNull("t", "third", "integer")));
        final ForeignKey key = ForeignKey.of("t", "c_t_fk", columns);

        columns.add(Column.ofNotNull("t", "fourth", "integer"));

        assertThat(key.getColumns())
            .hasSize(3)
            .doesNotContain(Column.ofNotNull("t", "fourth", "integer"))
            .isUnmodifiable();
    }

    @Test
    void allColumnMustBelongToTheSameTable() {
        final List<Column> columns = List.of(
            Column.ofNotNull("t", "first", "integer"),
            Column.ofNotNull("t1", "second", "integer"));
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_fk", columns))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name is not the same within given rows");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
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
        assertThatThrownBy(() -> ForeignKey.ofColumn("t", "fk", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", null, "integer"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", "", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", "  ", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", null, "integer"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", "", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", "  ", "integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        final Column column = Column.ofNotNull("t", "col", "integer");
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
        final ForeignKey first = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofNotNull("t", "order_id", "integer"), Column.ofNotNull("t", "limit", "integer")));
        final ForeignKey theSame = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofNotNull("t", "order_id", "integer"), Column.ofNotNull("t", "limit", "integer")));
        final ForeignKey withDifferentOrderOfColumns = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofNotNull("t", "limit", "integer"), Column.ofNotNull("t", "order_id", "integer")));
        final ForeignKey withDifferentColumnName = ForeignKey.ofNullableColumn("t", "c_t_order_id", "no_matter_what", "integer");

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
            List.of(Column.ofNotNull("table", "order_id", "integer"), Column.ofNotNull("table", "limit", "integer")));
        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        final ForeignKey fourth = ForeignKey.of("t", "other_id",
            List.of(Column.ofNotNull("t", "order_id", "integer"), Column.ofNotNull("t", "limit", "integer")));
        assertThat(fourth)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void factoriesWithEnumTypeShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(ForeignKey.ofNotNullColumn("t", "c_t_order_id", "order_id", PgColumnType.BIGINT))
            .usingRecursiveComparison()
            .isEqualTo(ForeignKey.ofNotNullColumn("t", "c_t_order_id", "order_id", "bigint"));
        assertThat(ForeignKey.ofNotNullColumn(ctx, "t", "c_t_order_id", "order_id", PgColumnType.BIGINT))
            .usingRecursiveComparison()
            .isEqualTo(ForeignKey.ofNotNullColumn(ctx, "t", "c_t_order_id", "order_id", "bigint"));
        assertThat(ForeignKey.ofNullableColumn("t", "c_t_order_id", "order_id", PgColumnType.UUID))
            .usingRecursiveComparison()
            .isEqualTo(ForeignKey.ofNullableColumn("t", "c_t_order_id", "order_id", "uuid"));
        assertThat(ForeignKey.ofNullableColumn(ctx, "t", "c_t_order_id", "order_id", PgColumnType.UUID))
            .usingRecursiveComparison()
            .isEqualTo(ForeignKey.ofNullableColumn(ctx, "t", "c_t_order_id", "order_id", "uuid"));
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
        final ForeignKey first = ForeignKey.ofNotNullColumn("t1", "c1", "col1", "integer");
        final ForeignKey theSame = ForeignKey.ofNotNullColumn("t1", "c1", "col2", "integer"); // different column!
        final ForeignKey secondName = ForeignKey.ofNotNullColumn("t1", "c2", "col1", "integer");
        final ForeignKey secondTable = ForeignKey.ofNotNullColumn("t2", "c1", "col1", "integer");

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
