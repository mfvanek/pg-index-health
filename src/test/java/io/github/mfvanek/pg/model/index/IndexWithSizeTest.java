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

class IndexWithSizeTest {

    @Test
    void indexWithZeroSize() {
        final IndexWithSize index = IndexWithSize.of("t", "i", 0L);
        assertThat(index.getIndexSizeInBytes()).isEqualTo(0L);
    }

    @Test
    void indexWithPositiveSize() {
        final IndexWithSize index = IndexWithSize.of("t", "i", 123L);
        assertThat(index.getIndexSizeInBytes()).isEqualTo(123L);
    }

    @Test
    void indexWithNegativeSize() {
        assertThatThrownBy(() -> IndexWithSize.of("t", "i", -1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testToString() {
        final IndexWithSize index = IndexWithSize.of("t", "i", 33L);
        assertThat(index.toString()).isEqualTo("IndexWithSize{tableName='t', indexName='i', indexSizeInBytes=33}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final IndexWithSize first = IndexWithSize.of("t1", "i1", 22L);
        final IndexWithSize theSame = IndexWithSize.of("t1", "i1", 44L); // different size!
        final IndexWithSize second = IndexWithSize.of("t1", "i2", 33L);
        final IndexWithSize third = IndexWithSize.of("t3", "i3", 22L);

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(BigDecimal.ZERO)).isFalse();

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
        final Index another = Index.of("t1", "i1");
        assertThat(another).isEqualTo(first);
        assertThat(another.hashCode()).isEqualTo(first.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(IndexWithSize.class)
                .withIgnoredFields("indexSizeInBytes")
                .verify();
    }

    @SuppressWarnings({"ConstantConditions", "EqualsWithItself", "ResultOfMethodCallIgnored"})
    @Test
    void compareToTest() {
        final IndexWithSize first = IndexWithSize.of("t1", "i1", 22L);
        final IndexWithSize theSame = IndexWithSize.of("t1", "i1", 44L); // different size!
        final IndexWithSize second = IndexWithSize.of("t1", "i2", 33L);
        final IndexWithSize third = IndexWithSize.of("t3", "i3", 22L);

        assertThatThrownBy(() -> first.compareTo(null)).isInstanceOf(NullPointerException.class);

        // self
        assertThat(first.compareTo(first)).isEqualTo(0);

        // the same
        assertThat(first.compareTo(theSame)).isEqualTo(0);

        // others
        assertThat(first.compareTo(second)).isEqualTo(-1);
        assertThat(second.compareTo(first)).isEqualTo(1);

        assertThat(second.compareTo(third)).isLessThanOrEqualTo(-1);
        assertThat(third.compareTo(second)).isGreaterThanOrEqualTo(1);
    }
}
