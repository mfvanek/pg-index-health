/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.table.Column;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ForeignKeyTest {

    @Test
    void testToString() {
        final ForeignKey foreignKey = ForeignKey.ofNotNullColumn("t", "c_t_order_id", "order_id");
        assertThat(foreignKey.toString())
                .isEqualTo("ForeignKey{tableName='t', constraintName='c_t_order_id', columnsInConstraint=[Column{tableName='t', columnName='order_id', notNull=true}]}");

        final ForeignKey foreignKeyWithNullableColumn = ForeignKey.ofNullableColumn("t", "c_t_order_id", "order_id");
        assertThat(foreignKeyWithNullableColumn.toString())
                .isEqualTo("ForeignKey{tableName='t', constraintName='c_t_order_id', columnsInConstraint=[Column{tableName='t', columnName='order_id', notNull=false}]}");
    }

    @Test
    void foreignKey() {
        final ForeignKey foreignKey = ForeignKey.ofNotNullColumn("t", "c_t_order_id", "order_id");
        assertThat(foreignKey.getTableName())
                .isNotBlank()
                .isEqualTo("t");
        assertThat(foreignKey.getConstraintName())
                .isNotBlank()
                .isEqualTo("c_t_order_id");
        assertThat(foreignKey.getColumnsInConstraint())
                .isNotNull()
                .hasSize(1)
                .containsExactly(Column.ofNotNull("t", "order_id"));
    }

    @Test
    void getColumnsInConstraint() {
        ForeignKey key = ForeignKey.of("t", "c_t_order_id",
                Arrays.asList(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "item_id")));
        assertThat(key.getColumnsInConstraint())
                .isNotNull()
                .hasSize(2)
                .containsExactly(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "item_id"));
    }

    @Test
    void shouldCreateDefensiveCopyOfColumnsList() {
        final List<Column> columns = new ArrayList<>(Arrays.asList(
                Column.ofNotNull("t", "first"),
                Column.ofNotNull("t", "second"),
                Column.ofNotNull("t", "third")));
        final ForeignKey key = ForeignKey.of("t", "c_t_fk", columns);

        columns.add(Column.ofNotNull("t", "fourth"));

        assertThat(key.getColumnsInConstraint())
                .isNotNull()
                .hasSize(3)
                .doesNotContain(Column.ofNotNull("t", "fourth"));

        assertThatThrownBy(() -> key.getColumnsInConstraint().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void allColumnMustBelongToTheSameTable() {
        final List<Column> columns = Arrays.asList(
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
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ForeignKey.of("t", null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_order_id", null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_order_id", Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ForeignKey.ofColumn("t", "fk", null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ForeignKey.ofNotNullColumn("t", "fk", "  "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ForeignKey.ofNullableColumn("t", "fk", "  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final ForeignKey first = ForeignKey.of("t", "c_t_order_id",
                Arrays.asList(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "limit")));
        final ForeignKey theSame = ForeignKey.of("t", "c_t_order_id",
                Arrays.asList(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "limit")));
        final ForeignKey withDifferentOrderOfColumns = ForeignKey.of("t", "c_t_order_id",
                Arrays.asList(Column.ofNotNull("t", "limit"), Column.ofNotNull("t", "order_id")));
        final ForeignKey second = ForeignKey.ofNullableColumn("t", "c_t_order_id", "no_matter_what");

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(BigDecimal.ZERO)).isFalse();

        // self
        assertThat(first).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(first.hashCode());

        // the same
        assertThat(theSame).isEqualTo(first);
        assertThat(theSame.hashCode()).isEqualTo(first.hashCode());

        // column order matters
        assertThat(withDifferentOrderOfColumns).isNotEqualTo(first);
        assertThat(withDifferentOrderOfColumns.hashCode()).isNotEqualTo(first.hashCode());

        assertThat(second).isNotEqualTo(first);
        assertThat(second.hashCode()).isNotEqualTo(first.hashCode());

        final ForeignKey third = ForeignKey.of("table", "c_t_order_id",
                Arrays.asList(Column.ofNotNull("table", "order_id"), Column.ofNotNull("table", "limit")));
        assertThat(third).isNotEqualTo(first);
        assertThat(third.hashCode()).isNotEqualTo(first.hashCode());

        final ForeignKey fourth = ForeignKey.of("t", "other_id",
                Arrays.asList(Column.ofNotNull("t", "order_id"), Column.ofNotNull("t", "limit")));
        assertThat(fourth).isNotEqualTo(first);
        assertThat(fourth.hashCode()).isNotEqualTo(first.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ForeignKey.class)
                .verify();
    }
}
