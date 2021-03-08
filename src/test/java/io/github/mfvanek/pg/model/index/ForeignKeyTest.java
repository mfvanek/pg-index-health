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
        // equals part
        ForeignKey key11 = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit"));
        ForeignKey key12 = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit"));
        ForeignKey key13 = ForeignKey.of("t", "c_t_order_id", Arrays.asList("limit", "order_id"));
        ForeignKey key14 = ForeignKey.ofColumn("t", "c_t_order_id", "no_matter_what");

        assertNotEquals(key11, null);
        assertNotEquals(key11, BigDecimal.ZERO);

        assertEquals(key11, key11);
        assertEquals(key11.hashCode(), key11.hashCode());
        assertEquals(key11, key12);
        assertEquals(key11.hashCode(), key12.hashCode());
        assertEquals(key11, key13);
        assertEquals(key11.hashCode(), key13.hashCode());
        assertEquals(key11, key14);
        assertEquals(key11.hashCode(), key14.hashCode());

        // not equals part
        ForeignKey key21 = ForeignKey.of("table", "c_t_order_id", Arrays.asList("order_id", "limit"));
        ForeignKey key22 = ForeignKey.of("t", "other_id", Arrays.asList("order_id", "limit"));

        assertNotEquals(key11, key21);
        assertNotEquals(key11.hashCode(), key21.hashCode());
        assertNotEquals(key11, key22);
        assertNotEquals(key11.hashCode(), key22.hashCode());
    }
}
