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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnusedIndexTest {

    @Test
    void getIndexScans() {
        final UnusedIndex index = UnusedIndex.of("t", "i", 1L, 2L);
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getIndexName()).isEqualTo("i");
        assertThat(index.getIndexSizeInBytes()).isEqualTo(1L);
        assertThat(index.getIndexScans()).isEqualTo(2L);
    }

    @Test
    void testToString() {
        final UnusedIndex index = UnusedIndex.of("t", "i", 1L, 2L);
        assertThat(index.toString()).isEqualTo("UnusedIndex{tableName='t', indexName='i', " + "indexSizeInBytes=1, indexScans=2}")
        ;
    }

    @Test
    void indexWithNegativeScans() {
        assertThatThrownBy(() -> UnusedIndex.of("t", "i", -1L, 0L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> UnusedIndex.of("t", "i", 1L, -1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testEqualsAndHashCode() {
        final UnusedIndex first = UnusedIndex.of("t1", "i1", 1L, 2L);
        final UnusedIndex theSame = UnusedIndex.of("t1", "i1", 10L, 6L); // different size!
        final UnusedIndex second = UnusedIndex.of("t1", "i2", 1L, 3L);
        final UnusedIndex third = UnusedIndex.of("t2", "i3", 2L, 2L);

        assertThat(first).isNotNull();
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(BigDecimal.ZERO).isNotEqualTo(first);

        // self
        assertThat(first).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(first.hashCode());

        // the same
        assertThat(theSame).isEqualTo(first);
        assertThat(theSame.hashCode()).isEqualTo(first.hashCode());

        // others
        assertThat(second).isNotEqualTo(first);
        assertThat(first).isNotEqualTo(second);
        assertThat(second.hashCode()).isNotEqualTo(first.hashCode());

        assertThat(third).isNotEqualTo(first);
        assertThat(third.hashCode()).isNotEqualTo(first.hashCode());

        assertThat(third).isNotEqualTo(second);
        assertThat(third.hashCode()).isNotEqualTo(second.hashCode());

        // another
        final Index anotherType = Index.of("t1", "i1");
        assertThat(anotherType).isEqualTo(first);
        assertThat(anotherType.hashCode()).isEqualTo(first.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(UnusedIndex.class)
                .withIgnoredFields("indexSizeInBytes", "indexScans")
                .verify();
    }
}
