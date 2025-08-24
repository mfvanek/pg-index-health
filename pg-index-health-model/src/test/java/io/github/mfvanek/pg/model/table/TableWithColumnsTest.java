/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableWithColumnsTest {

    @Test
    void gettersShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        final TableWithColumns first = TableWithColumns.ofNullableColumn(ctx, "t1", "c1");
        assertThat(first.getTableName())
            .isEqualTo("tst.t1")
            .isEqualTo(first.getName());
        assertThat(first.getTableSizeInBytes())
            .isZero();
        assertThat(first.getObjectType())
            .isEqualTo(PgObjectType.TABLE);
        assertThat(first.getColumns())
            .hasSize(1)
            .containsExactly(Column.ofNullable("tst.t1", "c1"))
            .isUnmodifiable();

        final TableWithColumns second = TableWithColumns.withoutColumns(ctx, "t1");
        assertThat(second.getColumns())
            .isEmpty();
        assertThat(second.getFirstColumn())
            .isNull();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void invalidArguments() {
        assertThatThrownBy(() -> TableWithColumns.withoutColumns(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("table cannot be null");
        assertThatThrownBy(() -> TableWithColumns.withoutColumns(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");
        final PgContext ctx = PgContext.of("tst");
        assertThatThrownBy(() -> TableWithColumns.withoutColumns(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("objectName cannot be null");
        assertThatThrownBy(() -> TableWithColumns.withoutColumns(ctx, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("objectName cannot be blank");
        assertThatThrownBy(() -> TableWithColumns.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("table cannot be null");
        final Table t = Table.of(ctx, "t");
        assertThatThrownBy(() -> TableWithColumns.of(t, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columns cannot be null");
        final List<Column> columns = List.of(Column.ofNullable("t1", "c1"));
        assertThatThrownBy(() -> TableWithColumns.of(t, columns))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name is not the same within given rows");
    }

    @Test
    void testToString() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(TableWithColumns.ofNotNullColumn(ctx, "t1", "c1"))
            .hasToString("TableWithColumns{tableName='tst.t1', tableSizeInBytes=0, columns=[Column{tableName='tst.t1', columnName='c1', notNull=true}]}");
        assertThat(TableWithColumns.withoutColumns(Table.of("t2")))
            .hasToString("TableWithColumns{tableName='t2', tableSizeInBytes=0, columns=[]}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final PgContext ctx = PgContext.of("tst");
        final TableWithColumns first = TableWithColumns.ofNotNullColumn(ctx, "t1", "c1");
        final TableWithColumns theSame = TableWithColumns.ofSingle(Table.of(ctx, "t1"), Column.ofNotNull(ctx, "t1", "c1"));
        final TableWithColumns third = TableWithColumns.ofNullableColumn(ctx, "t1", "c2");
        final TableWithColumns forth = TableWithColumns.ofNullableColumn(ctx, "t2", "c1");

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

        // different columns
        assertThat(third)
            .isEqualTo(first)
            .hasSameHashCodeAs(first)
            .isEqualTo(theSame)
            .hasSameHashCodeAs(theSame);

        // others
        assertThat(forth)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first)
            .isNotEqualTo(theSame)
            .doesNotHaveSameHashCodeAs(theSame);

        // another Table
        final TableWithBloat anotherType = TableWithBloat.of("tst.t1", 0L, 11L, 50);
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(anotherType)
            .isNotEqualTo(first)
            .hasSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(TableWithColumns.class)
            .withIgnoredFields(ColumnsAware.COLUMNS_FIELD)
            .verify();
    }

    @Test
    void compareToTest() {
        final PgContext ctx = PgContext.of("tst");
        final TableWithColumns first = TableWithColumns.ofNotNullColumn(ctx, "t1", "c1");
        final TableWithColumns theSame = TableWithColumns.ofSingle(Table.of(ctx, "t1"), Column.ofNotNull(ctx, "t1", "c1"));
        final TableWithColumns third = TableWithColumns.ofNullableColumn(ctx, "t2", "c1");

        assertThat(first)
            .isEqualByComparingTo(first)
            .isEqualByComparingTo(theSame)
            .isLessThan(third);

        assertThat(third)
            .isGreaterThan(first)
            .isGreaterThan(theSame);
    }
}
