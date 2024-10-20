/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.object.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexWithSizeTest {

    @Test
    void indexWithZeroSize() {
        final IndexWithSize index = IndexWithSize.of("t", "i", 0L);
        assertThat(index.getIndexName())
            .isEqualTo("i")
            .isEqualTo(index.getName());
        assertThat(index.getIndexSizeInBytes()).isZero();
        assertThat(index.getObjectType())
            .isEqualTo(PgObjectType.INDEX);
    }

    @Test
    void indexWithPositiveSize() {
        final IndexWithSize index = IndexWithSize.of("t", "i", 123L);
        assertThat(index.getIndexSizeInBytes()).isEqualTo(123L);
    }

    @Test
    void indexWithNegativeSize() {
        assertThatThrownBy(() -> IndexWithSize.of("t", "i", -1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexSizeInBytes cannot be less than zero");
    }

    @Test
    void testToString() {
        assertThat(IndexWithSize.of("t", "i", 33L))
            .hasToString("IndexWithSize{tableName='t', indexName='i', indexSizeInBytes=33}");
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
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first)
            .isNotEqualTo(second)
            .doesNotHaveSameHashCodeAs(second);

        // another
        final Index another = Index.of("t1", "i1");
        assertThat(another)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(IndexWithSize.class)
            .withIgnoredFields("indexSizeInBytes")
            .verify();
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    @Test
    void compareToTest() {
        final IndexWithSize first = IndexWithSize.of("t1", "i1", 22L);
        final IndexWithSize theSame = IndexWithSize.of("t1", "i1", 44L); // different size!
        final IndexWithSize second = IndexWithSize.of("t1", "i2", 33L);
        final IndexWithSize third = IndexWithSize.of("t3", "i3", 22L);

        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("other cannot be null");

        assertThat(first)
            .isEqualByComparingTo(first) // self
            .isEqualByComparingTo(theSame) // the same
            .isLessThan(second)
            .isLessThan(third);

        assertThat(second)
            .isGreaterThan(first)
            .isLessThan(third);

        assertThat(third)
            .isGreaterThan(first)
            .isGreaterThan(second);
    }
}
