/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DuplicatedIndexesTest {

    @Test
    void withTheSameTable() {
        final DuplicatedIndexes index = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertNotNull(index);
        assertEquals("t", index.getTableName());
        assertEquals(303L, index.getTotalSize());
        assertThat(index.getIndexNames(), contains("i1", "i2"));
    }

    @Test
    void ordering() {
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t1", "i3", 303L),
                IndexWithSize.of("t1", "i1", 101L),
                IndexWithSize.of("t1", "i2", 202L)));
        assertNotNull(indexes);
        assertThat(indexes.getDuplicatedIndexes(),
                contains(IndexWithSize.of("t1", "i1", 101L),
                        IndexWithSize.of("t1", "i2", 202L),
                        IndexWithSize.of("t1", "i3", 303L)));
    }

    @Test
    void testToString() {
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t", "i3", 303L),
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertNotNull(indexes);
        assertEquals("DuplicatedIndexes{tableName='t', totalSize=606, indexes=[" +
                        "IndexWithSize{tableName='t', indexName='i1', indexSizeInBytes=101}, " +
                        "IndexWithSize{tableName='t', indexName='i2', indexSizeInBytes=202}, " +
                        "IndexWithSize{tableName='t', indexName='i3', indexSizeInBytes=303}]}",
                indexes.toString());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withoutIndexes() {
        assertThrows(NullPointerException.class, () -> DuplicatedIndexes.of(null));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of(
                Collections.singletonList(IndexWithSize.of("t", "i", 1L))));
    }

    @Test
    void withDifferentTables() {
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t1", "i1", 1L),
                IndexWithSize.of("t2", "i2", 2L))));
    }

    @Test
    void fromValidString() {
        final DuplicatedIndexes index = DuplicatedIndexes.of("t", "idx=i3, size=11; idx=i4, size=167");
        assertNotNull(index);
        assertEquals("t", index.getTableName());
        assertEquals(178L, index.getTotalSize());
        assertThat(index.getIndexNames(), contains("i3", "i4"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void fromInvalidString() {
        assertThrows(NullPointerException.class, () -> DuplicatedIndexes.of(null, null));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of("", null));
        assertThrows(NullPointerException.class, () -> DuplicatedIndexes.of("t", null));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of("t", ""));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of("t", "i"));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of("t", "idx=i1, size=1"));
    }

    @Test
    void testEqualsAndHashCode() {
        final DuplicatedIndexes first = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t1", "i1", 101L),
                IndexWithSize.of("t1", "i2", 202L)));
        final DuplicatedIndexes second = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t1", "i3", 301L),
                IndexWithSize.of("t1", "i4", 402L)));
        final DuplicatedIndexes third = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t2", "i5", 101L),
                IndexWithSize.of("t2", "i6", 202L)));

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, DuplicatedIndexes.of(
                IndexWithSize.of("t1", "i2", 505L), // different order
                IndexWithSize.of("t1", "i1", 606L))); // different size

        // others
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(second, third);
        assertNotEquals(second.hashCode(), third.hashCode());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void newFactoryConstructor() {
        assertThrows(NullPointerException.class, () ->
                DuplicatedIndexes.of(null, null));
        assertThrows(NullPointerException.class, () ->
                DuplicatedIndexes.of(IndexWithSize.of("t", "i1", 1L), null));
        assertThrows(NullPointerException.class, () ->
                DuplicatedIndexes.of(
                        IndexWithSize.of("t", "i1", 1L),
                        IndexWithSize.of("t", "i2", 2L),
                        null,
                        IndexWithSize.of("t", "i4", 4L)));
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(
                IndexWithSize.of("t", "i3", 3L),
                IndexWithSize.of("t", "i1", 1L),
                IndexWithSize.of("t", "i2", 2L),
                IndexWithSize.of("t", "i4", 4L));
        assertNotNull(indexes);
        assertThat(indexes.getDuplicatedIndexes(), contains(
                IndexWithSize.of("t", "i1", 1L),
                IndexWithSize.of("t", "i2", 2L),
                IndexWithSize.of("t", "i3", 3L),
                IndexWithSize.of("t", "i4", 4L)
        ));
    }
}
