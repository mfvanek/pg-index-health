/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DuplicatedIndexesTest {

    @Test
    void withTheSameTable() {
        final var duplicatedIndexes = DuplicatedIndexes.of(List.of(
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertNotNull(duplicatedIndexes);
        assertEquals("t", duplicatedIndexes.getTableName());
        assertEquals(303L, duplicatedIndexes.getTotalSize());
        assertLinesMatch(List.of("i1", "i2"), duplicatedIndexes.getIndexNames());
    }

    @Test
    void testToString() {
        final var duplicatedIndexes = DuplicatedIndexes.of(List.of(
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertNotNull(duplicatedIndexes);
        assertEquals("DuplicatedIndexes{tableName='t', totalSize=303, indexes=[" +
                        "IndexWithSize{tableName='t', indexName='i1', indexSizeInBytes=101}, " +
                        "IndexWithSize{tableName='t', indexName='i2', indexSizeInBytes=202}]}",
                duplicatedIndexes.toString());
    }

    @Test
    void withoutIndexes() {
        assertThrows(NullPointerException.class, () -> DuplicatedIndexes.of(null));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of(
                Collections.singletonList(IndexWithSize.of("t", "i", 1L))));
    }

    @Test
    void withDifferentTables() {
        assertThrows(IllegalArgumentException.class, () -> DuplicatedIndexes.of(List.of(
                IndexWithSize.of("t1", "i1", 1L),
                IndexWithSize.of("t2", "i2", 2L))));
    }

    @Test
    void fromValidString() {
        final var duplicatedIndexes = DuplicatedIndexes.of("t", "idx=i3, size=11; idx=i4, size=167");
        assertNotNull(duplicatedIndexes);
        assertEquals("t", duplicatedIndexes.getTableName());
        assertEquals(178L, duplicatedIndexes.getTotalSize());
        assertLinesMatch(List.of("i3", "i4"), duplicatedIndexes.getIndexNames());
    }

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
