/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model;

import org.apache.commons.compress.utils.Sets;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForeignKeyTest {

    @Test
    void testToString() {
        final ForeignKey foreignKey = ForeignKey.of("t", "c_t_order_id",
                Collections.singletonList("order_id"));
        assertEquals("ForeignKey{tableName='t', constraintName='c_t_order_id', " +
                "columnsInConstraint=[order_id]}", foreignKey.toString());
    }

    @Test
    void foreignKey() {
        final ForeignKey foreignKey = ForeignKey.of("t", "c_t_order_id",
                Collections.singletonList("order_id"));
        assertEquals("t", foreignKey.getTableName());
        assertEquals("c_t_order_id", foreignKey.getConstraintName());
        assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("order_id"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> ForeignKey.of(null, null, null));
        assertThrows(NullPointerException.class, () -> ForeignKey.of("t", null, null));
        assertThrows(IllegalArgumentException.class, () -> ForeignKey.of("t", "c_t_order_id", null));
        assertThrows(IllegalArgumentException.class, () -> ForeignKey.of("t", "c_t_order_id", Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> ForeignKey.of("t", "c_t_order_id", Collections.emptySet()));
    }

    @Test
    void testEquals() {
        ForeignKey key11 = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit"));
        ForeignKey key12 = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit"));
        ForeignKey key13 = ForeignKey.of("t", "c_t_order_id", Arrays.asList("limit", "order_id"));
        ForeignKey key14 = ForeignKey.of("t", "c_t_order_id", Sets.newHashSet("limit", "order_id"));

        ForeignKey key21 = ForeignKey.of("table", "c_t_order_id", Arrays.asList("order_id", "limit"));
        ForeignKey key22 = ForeignKey.of("t", "other_id", Arrays.asList("order_id", "limit"));
        ForeignKey key23 = ForeignKey.of("t", "c_t_order_id", Collections.singletonList("order_id"));
        ForeignKey key24 = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit_2"));
        ForeignKey key25 = ForeignKey.of("t", "c_t_order_id", Arrays.asList("order_id", "limit", "offset"));

        assertEquals(key11, key11);
        assertEquals(key11, key12);
        assertEquals(key11, key13);
        assertEquals(key11, key14);

        assertNotEquals(key11, key21);
        assertNotEquals(key11, key22);
        assertNotEquals(key11, key23);
        assertNotEquals(key11, key24);
        assertNotEquals(key11, key25);
    }
}
