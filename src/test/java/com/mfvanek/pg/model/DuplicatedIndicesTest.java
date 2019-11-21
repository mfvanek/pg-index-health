/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DuplicatedIndicesTest {

    @Test
    void withTheSameTable() {
        final var index = DuplicatedIndices.of(List.of(
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertNotNull(index);
        assertEquals("t", index.getTableName());
        assertEquals(303L, index.getTotalSize());
        assertThat(index.getDuplicatedIndices().stream()
                        .map(IndexWithSize::getIndexName)
                        .collect(Collectors.toList()),
                containsInAnyOrder("i1", "i2"));
    }

    @Test
    void testToString() {
        final var indices = DuplicatedIndices.of(List.of(
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertNotNull(indices);
        assertEquals("DuplicatedIndices{tableName='t', totalSize=303, indices=[" +
                        "IndexWithSize{tableName='t', indexName='i1', indexSizeInBytes=101}, " +
                        "IndexWithSize{tableName='t', indexName='i2', indexSizeInBytes=202}]}",
                indices.toString());
    }

    @Test
    void withoutIndices() {
        assertThrows(NullPointerException.class, () -> DuplicatedIndices.of(null));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndices.of(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndices.of(
                Collections.singletonList(IndexWithSize.of("t", "i", 1L))));
    }

    @Test
    void withDifferentTables() {
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndices.of(List.of(
                IndexWithSize.of("t1", "i1", 1L),
                IndexWithSize.of("t2", "i2", 2L))));
    }

    @Test
    void fromValidString() {
        final var index = DuplicatedIndices.of("t", "idx=i3, size=11; idx=i4, size=167");
        assertNotNull(index);
        assertEquals("t", index.getTableName());
        assertEquals(178L, index.getTotalSize());
        assertThat(index.getDuplicatedIndices().stream()
                        .map(IndexWithSize::getIndexName)
                        .collect(Collectors.toList()),
                containsInAnyOrder("i3", "i4"));
    }

    @Test
    void fromInvalidString() {
        assertThrows(NullPointerException.class, () -> DuplicatedIndices.of(null, null));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndices.of("", null));
        assertThrows(NullPointerException.class, () -> DuplicatedIndices.of("t", null));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndices.of("t", ""));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndices.of("t", "i"));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndices.of("t", "idx=i1, size=1"));
    }
}
