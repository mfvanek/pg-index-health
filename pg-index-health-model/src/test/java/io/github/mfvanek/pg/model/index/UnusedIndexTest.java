/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class UnusedIndexTest {

    @Test
    void getIndexScans() {
        final UnusedIndex index = UnusedIndex.of("t", "i", 1L, 2L);
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getIndexName())
                .isEqualTo("i")
                .isEqualTo(index.getName());
        assertThat(index.getIndexSizeInBytes()).isEqualTo(1L);
        assertThat(index.getIndexScans()).isEqualTo(2L);
    }

    @Test
    void testToString() {
        assertThat(UnusedIndex.of("t", "i", 1L, 2L))
                .hasToString("UnusedIndex{tableName='t', indexName='i', " + "indexSizeInBytes=1, indexScans=2}");
    }

    @Test
    void indexWithNegativeScans() {
        assertThatThrownBy(() -> UnusedIndex.of("t", "i", -1L, 0L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> UnusedIndex.of("t", "i", 1L, -1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final UnusedIndex first = UnusedIndex.of("t1", "i1", 1L, 2L);
        final UnusedIndex theSame = UnusedIndex.of("t1", "i1", 10L, 6L); // different size!
        final UnusedIndex second = UnusedIndex.of("t1", "i2", 1L, 3L);
        final UnusedIndex third = UnusedIndex.of("t2", "i3", 2L, 2L);

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(BigDecimal.ZERO)).isFalse();

        // self
        assertThat(first)
                .isEqualTo(first)
                .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
                .isEqualTo(first)
                .hasSameHashCodeAs(first);

        // others
        assertThat(second)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first)
                .isNotEqualTo(second)
                .doesNotHaveSameHashCodeAs(second);

        // another
        final Index anotherType = Index.of("t1", "i1");
        assertThat(anotherType)
                .isEqualTo(first)
                .hasSameHashCodeAs(first);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(UnusedIndex.class)
                .withIgnoredFields("indexSizeInBytes", "indexScans")
                .verify();
    }
}
