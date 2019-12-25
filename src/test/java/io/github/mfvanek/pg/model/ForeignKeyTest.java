/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForeignKeyTest {

    @Test
    void testToString() {
        final var foreignKey = ForeignKey.of("t", "c_t_order_id", List.of("order_id"));
        assertEquals("ForeignKey{tableName='t', constraintName='c_t_order_id', " +
                "columnsInConstraint=[order_id]}", foreignKey.toString());
    }

    @Test
    void foreignKey() {
        final var foreignKey = ForeignKey.of("t", "c_t_order_id", List.of("order_id"));
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
    }
}
