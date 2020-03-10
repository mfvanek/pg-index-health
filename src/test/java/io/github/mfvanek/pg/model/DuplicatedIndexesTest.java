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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertThat(index.getDuplicatedIndexes().stream()
                        .map(IndexWithSize::getIndexName)
                        .collect(Collectors.toList()),
                containsInAnyOrder("i1", "i2"));
    }

    @Test
    void testToString() {
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertNotNull(indexes);
        assertEquals("DuplicatedIndexes{tableName='t', totalSize=303, indexes=[" +
                        "IndexWithSize{tableName='t', indexName='i1', indexSizeInBytes=101}, " +
                        "IndexWithSize{tableName='t', indexName='i2', indexSizeInBytes=202}]}",
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
        assertThat(index.getDuplicatedIndexes().stream()
                        .map(IndexWithSize::getIndexName)
                        .collect(Collectors.toList()),
                containsInAnyOrder("i3", "i4"));
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
}
