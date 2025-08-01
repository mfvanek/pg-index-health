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

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableWithMissingIndexTest {

    @Test
    void gettersShouldWork() {
        final TableWithMissingIndex table = TableWithMissingIndex.of("t", 1L, 2L, 3L);
        assertThat(table.getTableName())
            .isEqualTo("t")
            .isEqualTo(table.getName());
        assertThat(table.getTableSizeInBytes())
            .isEqualTo(1L);
        assertThat(table.getSeqScans())
            .isEqualTo(2L);
        assertThat(table.getIndexScans())
            .isEqualTo(3L);
        assertThat(table.getObjectType())
            .isEqualTo(PgObjectType.TABLE);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void invalidArguments() {
        assertThatThrownBy(() -> TableWithMissingIndex.of(null, 0, 0, 0))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> TableWithMissingIndex.of("", 0, 0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> TableWithMissingIndex.of(" ", 0, 0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> TableWithMissingIndex.of("t", -1, 0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableSizeInBytes cannot be less than zero");
        assertThatThrownBy(() -> TableWithMissingIndex.of("t", 0, -1, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("seqScans cannot be less than zero");
        assertThatThrownBy(() -> TableWithMissingIndex.of("t", 0, 0, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexScans cannot be less than zero");
        assertThatThrownBy(() -> TableWithMissingIndex.of(null, 0L, 0L))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("table cannot be null");
        assertThatThrownBy(() -> TableWithMissingIndex.of(null, "t", 0L, 0L, 0L))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");
    }

    @Test
    void testToString() {
        assertThat(TableWithMissingIndex.of("t", 11L, 33L, 22L))
            .hasToString("TableWithMissingIndex{tableName='t', tableSizeInBytes=11, seqScans=33, indexScans=22}");
        assertThat(TableWithMissingIndex.of("t"))
            .hasToString("TableWithMissingIndex{tableName='t', tableSizeInBytes=0, seqScans=0, indexScans=0}");

        final PgContext ctx = PgContext.of("tst");
        assertThat(TableWithMissingIndex.of(ctx, "t", 11L, 33L, 22L))
            .hasToString("TableWithMissingIndex{tableName='tst.t', tableSizeInBytes=11, seqScans=33, indexScans=22}");
        assertThat(TableWithMissingIndex.of(ctx, "t"))
            .hasToString("TableWithMissingIndex{tableName='tst.t', tableSizeInBytes=0, seqScans=0, indexScans=0}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final TableWithMissingIndex first = TableWithMissingIndex.of("t1", 1L, 0, 1);
        final TableWithMissingIndex theSame = TableWithMissingIndex.of("t1", 2L, 2, 3);
        final TableWithMissingIndex third = TableWithMissingIndex.of("t2", 3L, 4, 5);

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

        // others
        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first)
            .isNotEqualTo(theSame)
            .doesNotHaveSameHashCodeAs(theSame);

        // another Table
        final TableWithBloat anotherType = TableWithBloat.of("t1", 4L, 11L, 50);
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(anotherType)
            .isNotEqualTo(first)
            .hasSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(TableWithMissingIndex.class)
            .withIgnoredFields("seqScans", "indexScans")
            .verify();
    }

    @Test
    void compareToTest() {
        final TableWithMissingIndex first = TableWithMissingIndex.of("t1", 1L, 0, 1);
        final TableWithMissingIndex theSame = TableWithMissingIndex.of("t1", 2L, 2, 3);
        final TableWithMissingIndex third = TableWithMissingIndex.of("t2", 3L, 4, 5);
        assertThat(first)
            .isEqualByComparingTo(first)
            .isEqualByComparingTo(theSame)
            .isLessThan(third);

        assertThat(third)
            .isGreaterThan(first)
            .isGreaterThan(theSame);
    }
}
