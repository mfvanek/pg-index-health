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
        final ForeignKey foreignKey = ForeignKey.ofColumn("t", "c_t_order_id", "order_id");
        assertThat(foreignKey.toString()).isEqualTo("ForeignKey{tableName='t', constraintName='c_t_order_id', " + "columnsInConstraint=[order_id]}")
        ;
    }

    @Test
    void foreignKey() {
        final ForeignKey foreignKey = ForeignKey.ofColumn("t", "c_t_order_id", "order_id");
        assertThat(foreignKey.getTableName()).isEqualTo("t");
        assertThat(foreignKey.getConstraintName()).isEqualTo("c_t_order_id");
        assertThat(foreignKey.getColumnsInConstraint()).contains("order_id");
    }

    @Test
    void getColumnsInConstraint() {
        ForeignKey key = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "item_id"));
        assertThat(key.getColumnsInConstraint()).contains("order_id", "item_id");
    }

    @Test
    void shouldCreateDefensiveCopyOfColumnsList() {
        final List<String> columns = new ArrayList<>(Arrays.asList("first", "second", "third"));
        final ForeignKey key = ForeignKey.of("t", "c_t_fk", columns);

        columns.add("fourth");
        assertThat(key.getColumnsInConstraint()).hasSize(3);
        assertThat(key.getColumnsInConstraint()).doesNotContain("fourth");
        assertThatThrownBy(() -> key.getColumnsInConstraint().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> ForeignKey.of(null, null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ForeignKey.of("t", null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_order_id", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ForeignKey.of("t", "c_t_order_id", Collections.emptyList())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ForeignKey.ofColumn("t", "fk", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ForeignKey.ofColumn("t", "fk", "")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ForeignKey.ofColumn("t", "fk", "  ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalsAndHashCode() {
        final ForeignKey first = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit"));
        final ForeignKey theSame = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit"));
        final ForeignKey withDifferentOrderOfColumns = ForeignKey.of("t", "c_t_order_id", Arrays.asList("limit", "order_id"));
        final ForeignKey second = ForeignKey.ofColumn("t", "c_t_order_id", "no_matter_what");

        assertThat(first).isNotNull();
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(BigDecimal.ZERO).isNotEqualTo(first);

        // self
        assertThat(first).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(first.hashCode());

        // the same
        assertThat(theSame).isEqualTo(first);
        assertThat(theSame.hashCode()).isEqualTo(first.hashCode());

        // others
        assertThat(withDifferentOrderOfColumns).isEqualTo(first);
        assertThat(withDifferentOrderOfColumns.hashCode()).isEqualTo(first.hashCode());

        assertThat(second).isEqualTo(first);
        assertThat(second.hashCode()).isEqualTo(first.hashCode());

        final ForeignKey third = ForeignKey.of("table", "c_t_order_id", Arrays.asList("order_id", "limit"));
        assertThat(third).isNotEqualTo(first);
        assertThat(third.hashCode()).isNotEqualTo(first.hashCode());

        final ForeignKey fourth = ForeignKey.of("t", "other_id", Arrays.asList("order_id", "limit"));
        assertThat(fourth).isNotEqualTo(first);
        assertThat(fourth.hashCode()).isNotEqualTo(first.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ForeignKey.class)
                .withIgnoredFields("columnsInConstraint")
                .verify();
    }
}
