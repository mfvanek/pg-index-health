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

class IndexWithNullsTest {

    @Test
    void getNullableField() {
        final IndexWithNulls index = IndexWithNulls.of("t", "i", 11L, "f");
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getIndexName()).isEqualTo("i");
        assertThat(index.getIndexSizeInBytes()).isEqualTo(11L);
        assertThat(index.getNullableField()).isEqualTo("f");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> IndexWithNulls.of(null, null, 0, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> IndexWithNulls.of("", null, 0, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> IndexWithNulls.of("  ", null, 0, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> IndexWithNulls.of("t", null, 0, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> IndexWithNulls.of("t", "", 0, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> IndexWithNulls.of("t", "i", 0, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> IndexWithNulls.of("t", "i", 0, "")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> IndexWithNulls.of("t", "i", 0, "  ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testToString() {
        final IndexWithNulls index = IndexWithNulls.of("t", "i", 22L, "f");
        assertThat(index.toString()).isEqualTo("IndexWithNulls{tableName='t', indexName='i', " + "indexSizeInBytes=22, nullableField='f'}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final IndexWithNulls first = IndexWithNulls.of("t1", "i1", 1, "f");
        final IndexWithNulls theSame = IndexWithNulls.of("t1", "i1", 3, "f"); // different size!
        final IndexWithNulls second = IndexWithNulls.of("t2", "i2", 2, "f");
        final IndexWithNulls third = IndexWithNulls.of("t3", "i3", 2, "t");

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
        final Index anotherType = Index.of("t1", "i1");
        assertThat(anotherType).isEqualTo(first);
        assertThat(anotherType.hashCode()).isEqualTo(first.hashCode());

    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(IndexWithNulls.class)
                .withIgnoredFields("indexSizeInBytes", "nullableField")
                .verify();
    }
}
