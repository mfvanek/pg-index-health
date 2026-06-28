/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class ColumnsWithInconsistentTypesCheckOnHostUnitTest {

    @Test
    void shouldReturnEmptyForEmptyInput() {
        assertThat(ColumnsWithInconsistentTypesCheckOnHost.removeConsistentColumns(List.of()))
            .isEmpty();
    }

    @Test
    void shouldDropColumnNameLeftWithSingleType() {
        final ColumnWithType warehouseId = ColumnWithType.ofBigint(Column.ofNotNull("warehouse", "id"));

        assertThat(ColumnsWithInconsistentTypesCheckOnHost.removeConsistentColumns(List.of(warehouseId)))
            .as("A single column cannot be inconsistent with itself")
            .isEmpty();
    }

    @Test
    void shouldDropColumnNameWhenAllRemainingTypesAreEqual() {
        final ColumnWithType accountsId = ColumnWithType.ofBigint(Column.ofNotNull("accounts", "id"));
        final ColumnWithType clientsId = ColumnWithType.ofBigint(Column.ofNotNull("clients", "id"));

        assertThat(ColumnsWithInconsistentTypesCheckOnHost.removeConsistentColumns(List.of(accountsId, clientsId)))
            .as("Same name with a single distinct type is consistent")
            .isEmpty();
    }

    @Test
    void shouldKeepColumnNameWithMoreThanOneType() {
        final ColumnWithType accountsId = ColumnWithType.ofBigint(Column.ofNotNull("accounts", "id"));
        final ColumnWithType clientsId = ColumnWithType.ofInteger(Column.ofNotNull("clients", "id"));

        assertThat(ColumnsWithInconsistentTypesCheckOnHost.removeConsistentColumns(List.of(accountsId, clientsId)))
            .containsExactly(accountsId, clientsId);
    }

    @Test
    void shouldKeepInconsistentGroupAndDropDegradedOnePreservingOrder() {
        final ColumnWithType createdAt = ColumnWithType.ofTimestamp(Column.ofNotNull("t1", "created_at"));
        final ColumnWithType accountsId = ColumnWithType.ofBigint(Column.ofNotNull("accounts", "id"));
        final ColumnWithType clientsId = ColumnWithType.ofInteger(Column.ofNotNull("clients", "id"));

        assertThat(ColumnsWithInconsistentTypesCheckOnHost.removeConsistentColumns(List.of(createdAt, accountsId, clientsId)))
            .as("created_at has a single type and is dropped; id keeps two types and survives in original order")
            .containsExactly(accountsId, clientsId);
    }
}
