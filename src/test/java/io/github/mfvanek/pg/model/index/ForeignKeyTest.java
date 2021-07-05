/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
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
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForeignKeyTest {

    @Test
    void testToString() {
        final ForeignKey foreignKey = ForeignKey.ofColumn("t", "c_t_order_id", "order_id");
        assertEquals("ForeignKey{tableName='t', constraintName='c_t_order_id', " +
                "columnsInConstraint=[order_id]}", foreignKey.toString());
    }

    @Test
    void foreignKey() {
        final ForeignKey foreignKey = ForeignKey.ofColumn("t", "c_t_order_id", "order_id");
        assertEquals("t", foreignKey.getTableName());
        assertEquals("c_t_order_id", foreignKey.getConstraintName());
        assertThat(foreignKey.getColumnsInConstraint(), contains("order_id"));
    }

    @Test
    void getColumnsInConstraint() {
        ForeignKey key = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "item_id"));
        assertThat(key.getColumnsInConstraint(), contains("order_id", "item_id"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> ForeignKey.of(null, null, null));
        assertThrows(NullPointerException.class, () -> ForeignKey.of("t", null, null));
        assertThrows(IllegalArgumentException.class, () -> ForeignKey.of("t", "c_t_order_id", null));
        assertThrows(IllegalArgumentException.class, () -> ForeignKey.of("t", "c_t_order_id", Collections.emptyList()));
        assertThrows(NullPointerException.class, () -> ForeignKey.ofColumn("t", "fk", null));
        assertThrows(IllegalArgumentException.class, () -> ForeignKey.ofColumn("t", "fk", ""));
        assertThrows(IllegalArgumentException.class, () -> ForeignKey.ofColumn("t", "fk", "  "));
    }

    @Test
    void equalsAndHashCode() {
        final ForeignKey first = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit"));
        final ForeignKey theSame = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit"));
        final ForeignKey withDifferentOrderOfColumns = ForeignKey.of("t", "c_t_order_id", Arrays.asList("limit", "order_id"));
        final ForeignKey second = ForeignKey.ofColumn("t", "c_t_order_id", "no_matter_what");

        assertNotEquals(first, null);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, theSame);
        assertEquals(first.hashCode(), theSame.hashCode());

        // others
        assertEquals(first, withDifferentOrderOfColumns);
        assertEquals(first.hashCode(), withDifferentOrderOfColumns.hashCode());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        final ForeignKey third = ForeignKey.of("table", "c_t_order_id", Arrays.asList("order_id", "limit"));
        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        final ForeignKey fourth = ForeignKey.of("t", "other_id", Arrays.asList("order_id", "limit"));
        assertNotEquals(first, fourth);
        assertNotEquals(first.hashCode(), fourth.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ForeignKey.class)
                .withIgnoredFields("columnsInConstraint")
                .verify();
    }
}
