/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.column.Column;
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
        assertThat(ForeignKey.ofNotNullColumn("t", "c_t_order_id", "order_id"))
            .hasToString("ForeignKey{tableName='t', constraintName='c_t_order_id', columnsInConstraint=[Column{tableName='t', columnName='order_id', notNull=true}]}");

        final PgContext ctx = PgContext.of("tst");
        assertThat(ForeignKey.ofNotNullColumn(ctx, "t", "c_t_order_id", "order_id"))
            .hasToString("ForeignKey{tableName='tst.t', constraintName='c_t_order_id', columnsInConstraint=[Column{tableName='tst.t', columnName='order_id', notNull=true}]}");

        assertThat(ForeignKey.ofNullableColumn("t", "c_t_order_id", "order_id"))
            .hasToString("ForeignKey{tableName='t', constraintName='c_t_order_id', columnsInConstraint=[Column{tableName='t', columnName='order_id', notNull=false}]}");

        assertThat(ForeignKey.ofNullableColumn(ctx, "t", "c_t_order_id", "order_id"))
            .hasToString("ForeignKey{tableName='tst.t', constraintName='c_t_order_id', columnsInConstraint=[Column{tableName='tst.t', columnName='order_id', notNull=false}]}");
    }

    @Test
    void foreignKey() {
        final ForeignKey foreignKey = ForeignKey.ofNotNullColumn("t", "c_t_order_id", "order_id");
        assertThat(foreignKey.getTableName())
            .isNotBlank()
            .isEqualTo("t");
        assertThat(foreignKey.getConstraintName())
            .isNotBlank()
            .isEqualTo("c_t_order_id")
            .isEqualTo(foreignKey.getName());
        assertThat(foreignKey.getColumnsInConstraint())
            .hasSize(1)
            .containsExactly(Column.ofNotNull("t", "order_id"))
            .isUnmodifiable();
        assertThat(foreignKey.getObjectType())
            .isEqualTo(PgObjectType.CONSTRAINT);
    }

    @Test
    void getColumnsInConstraint() {
        final ForeignKey first = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "item_id")));
        assertThat(first.getColumnsInConstraint())
            .hasSize(2)
            .containsExactly(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "item_id"))
            .isUnmodifiable();

        final PgContext ctx = PgContext.of("tst");
        final ForeignKey second = ForeignKey.of(ctx, "t", "c_t_order_id",
            List.of(Column.ofNotNull(ctx, "t", "order_id"), Column.ofNotNull(ctx, "t", "item_id")));
        assertThat(second.getColumnsInConstraint())
            .hasSize(2)
            .containsExactly(Column.ofNotNull("tst.t", "order_id"), Column.ofNotNull("tst.t", "item_id"))
            .isUnmodifiable();
        assertThat(second.getTableName())
            .isEqualTo("tst.t");
    }

    @Test
    void shouldCreateDefensiveCopyOfColumnsList() {
        final List<Column> columns = new ArrayList<>(List.of(
            Column.ofNotNull("t", "first"),
            Column.ofNotNull("t", "second"),
            Column.ofNotNull("t", "third")));
        final ForeignKey key = ForeignKey.of("t", "c_t_fk", columns);

        columns.add(Column.ofNotNull("t", "fourth"));

        assertThat(key.getColumnsInConstraint())
            .hasSize(3)
            .doesNotContain(Column.ofNotNull("t", "fourth"))
            .isUnmodifiable();
    }

    @Test
    void allColumnMustBelongToTheSameTable() {
        final List<Column> columns = List.of(
            Column.ofNotNull("t", "first"),
            Column.ofNotNull("t1", "second"));
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
            .hasMessage("columnsInConstraint cannot be null");
        final List<Column> columns = List.of();
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_order_id", columns))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnsInConstraint cannot be empty");
        assertThatThrownBy(() -> ForeignKey.ofColumn("t", "fk", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", "  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", "  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnName cannot be blank");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final ForeignKey first = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "limit")));
        final ForeignKey theSame = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "limit")));
        final ForeignKey withDifferentOrderOfColumns = ForeignKey.of("t", "c_t_order_id",
            List.of(Column.ofNotNull("t", "limit"), Column.ofNotNull("t", "order_id")));
        final ForeignKey withDifferentColumnName = ForeignKey.ofNullableColumn("t", "c_t_order_id", "no_matter_what");

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

        // column name doesn't matter
        assertThat(withDifferentColumnName)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        final ForeignKey third = ForeignKey.of("table", "c_t_order_id",
            List.of(Column.ofNotNull("table", "order_id"), Column.ofNotNull("table", "limit")));
        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        final ForeignKey fourth = ForeignKey.of("t", "other_id",
            List.of(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "limit")));
        assertThat(fourth)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ForeignKey.class)
            .withIgnoredFields("constraintType", "columnsInConstraint")
            .verify();
    }
}
