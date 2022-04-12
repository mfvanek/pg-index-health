/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DuplicatedIndexesTest {

    @Test
    void withTheSameTable() {
        final DuplicatedIndexes index = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertThat(index).isNotNull();
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getTotalSize()).isEqualTo(303L);
        assertThat(index.getIndexNames()).contains("i1", "i2");
    }

    @Test
    void ordering() {
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t1", "i3", 303L),
                IndexWithSize.of("t1", "i1", 101L),
                IndexWithSize.of("t1", "i2", 202L)));
        assertThat(indexes).isNotNull();

        assertThat(indexes.getTotalSize()).isEqualTo(606L);
        assertThat(indexes.getDuplicatedIndexes()).contains(
                IndexWithSize.of("t1", "i1", 101L),
                IndexWithSize.of("t1", "i2", 202L),
                IndexWithSize.of("t1", "i3", 303L)
        );
        assertThat(indexes.getIndexNames()).contains("i1", "i2", "i3");
    }

    @Test
    void shouldCreateDefensiveCopyOfIndexesList() {
        final List<IndexWithSize> sourceIndexes = new ArrayList<>(Arrays.asList(
                IndexWithSize.of("t1", "i3", 303L),
                IndexWithSize.of("t1", "i1", 101L),
                IndexWithSize.of("t1", "i2", 202L)));
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(sourceIndexes);

        final IndexWithSize fourth = IndexWithSize.of("t1", "i4", 404L);
        sourceIndexes.add(fourth);

        assertThat(indexes.getDuplicatedIndexes()).hasSize(3);
        assertThat(indexes.getDuplicatedIndexes()).doesNotContain(fourth);
        assertThatThrownBy(() -> indexes.getDuplicatedIndexes().clear()).isInstanceOf(UnsupportedOperationException.class);

        assertThat(indexes.getIndexNames()).hasSize(3);
        assertThat(indexes.getIndexNames()).doesNotContain("i4");
        assertThatThrownBy(() -> indexes.getIndexNames().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testToString() {
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(Arrays.asList(
                IndexWithSize.of("t", "i3", 303L),
                IndexWithSize.of("t", "i1", 101L),
                IndexWithSize.of("t", "i2", 202L)));
        assertThat(indexes).isNotNull();
        assertThat(indexes.toString()).isEqualTo("DuplicatedIndexes{tableName='t', totalSize=606, indexes=[" + "IndexWithSize{tableName='t', indexName='i1', indexSizeInBytes=101}, " + 
                "IndexWithSize{tableName='t', indexName='i2', indexSizeInBytes=202}, " + "IndexWithSize{tableName='t', indexName='i3', indexSizeInBytes=303}]}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withoutIndexes() {
        assertThatThrownBy(() -> DuplicatedIndexes.of(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of(Collections.emptyList())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of(Collections.singletonList(IndexWithSize.of("t", "i", 1L)))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withDifferentTables() {
        assertThatThrownBy(() -> DuplicatedIndexes.of(Arrays.asList(IndexWithSize.of("t1", "i1", 1L), IndexWithSize.of("t2", "i2", 2L)))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fromValidString() {
        final DuplicatedIndexes index = DuplicatedIndexes.of("t", "idx=i3, size=11; idx=i4, size=167");
        assertThat(index).isNotNull();
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getTotalSize()).isEqualTo(178L);
        assertThat(index.getIndexNames()).contains("i3", "i4");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void fromInvalidString() {
        assertThatThrownBy(() -> DuplicatedIndexes.of(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("t", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("t", "")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("t", "i")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("t", "idx=i1, size=1")).isInstanceOf(IllegalArgumentException.class);
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

        assertThat(first).isNotNull();
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(BigDecimal.ZERO).isNotEqualTo(first);

        // self
        assertThat(first).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(first.hashCode());

        // the same
        assertThat(
                DuplicatedIndexes.of(// different order
                        IndexWithSize.of("t1", "i2", 505L), // different size
                        IndexWithSize.of("t1", "i1", 606L)
                )
        ).isEqualTo(first); // different size

        // others
        assertThat(second).isNotEqualTo(first);
        assertThat(second.hashCode()).isNotEqualTo(first.hashCode());

        assertThat(third).isNotEqualTo(first);
        assertThat(third.hashCode()).isNotEqualTo(first.hashCode());

        assertThat(third).isNotEqualTo(second);
        assertThat(third.hashCode()).isNotEqualTo(second.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(DuplicatedIndexes.class)
                .withIgnoredFields("totalSize", "indexesNames")
                .verify();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void newFactoryConstructor() {
        assertThatThrownBy(() -> DuplicatedIndexes.of(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of(IndexWithSize.of("t", "i1", 1L), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of(IndexWithSize.of("t", "i1", 1L), IndexWithSize.of("t", "i2", 2L), null, IndexWithSize.of("t", "i4", 4L))).isInstanceOf(
                NullPointerException.class);
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(
                IndexWithSize.of("t", "i3", 3L),
                IndexWithSize.of("t", "i1", 1L),
                IndexWithSize.of("t", "i2", 2L),
                IndexWithSize.of("t", "i4", 4L));
        assertThat(indexes).isNotNull();
        assertThat(indexes.getDuplicatedIndexes()).contains(IndexWithSize.of("t", "i1", 1L), IndexWithSize.of("t", "i2", 2L), IndexWithSize.of("t", "i3", 3L), IndexWithSize.of("t", "i4", 4L));
    }
}
