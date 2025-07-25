/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexWithBloatTest {

    @Test
    void getBloatSizeInBytes() {
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 10L, 2L, 20);
        assertThat(bloat.getIndexName())
            .isEqualTo("i")
            .isEqualTo(bloat.getName());
        assertThat(bloat.getBloatSizeInBytes())
            .isEqualTo(2L);
        assertThat(bloat.getObjectType())
            .isEqualTo(PgObjectType.INDEX);
    }

    @Test
    void getBloatPercentage() {
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 5L, 1L, 25);
        assertThat(bloat.getBloatPercentage()).isEqualTo(25);
    }

    @Test
    void testToString() {
        assertThat(IndexWithBloat.of("t", "i", 2L, 1L, 50))
            .hasToString("IndexWithBloat{tableName='t', indexName='i', indexSizeInBytes=2, bloatSizeInBytes=1, bloatPercentage=50.0}");
        final PgContext ctx = PgContext.of("tst");
        assertThat(IndexWithBloat.of(ctx, "t", "i", 2L, 1L, 50))
            .hasToString("IndexWithBloat{tableName='tst.t', indexName='tst.i', indexSizeInBytes=2, bloatSizeInBytes=1, bloatPercentage=50.0}");
        assertThat(IndexWithBloat.of(ctx, "t", "i"))
            .hasToString("IndexWithBloat{tableName='tst.t', indexName='tst.i', indexSizeInBytes=0, bloatSizeInBytes=0, bloatPercentage=0.0}");
    }

    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> IndexWithBloat.of("t", "i", 0L, -1L, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("bloatSizeInBytes cannot be less than zero");
        assertThatThrownBy(() -> IndexWithBloat.of("t", "i", 0L, 0L, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("bloatPercentage should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> IndexWithBloat.of("t", "i", -1L, 0L, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexSizeInBytes cannot be less than zero");
        assertThat(IndexWithBloat.of("t", "i", 0L, 0L, 0))
            .isNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final IndexWithBloat first = IndexWithBloat.of("t1", "i1", 22L, 11L, 50);
        final IndexWithBloat theSame = IndexWithBloat.of("t1", "i1", 100L, 60L, 60); // different size!
        final IndexWithBloat second = IndexWithBloat.of("t2", "i2", 30L, 3L, 10);
        final IndexWithBloat third = IndexWithBloat.of("t3", "i3", 22L, 11L, 50);

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
        final Index anotherType = Index.of("t1", "i1");
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(anotherType)
            .isNotEqualTo(first)
            .hasSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(IndexWithBloat.class)
            .withIgnoredFields("bloatSizeInBytes", "bloatPercentage")
            .verify();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void compareToTest() {
        final IndexWithBloat first = IndexWithBloat.of("t1", "i1", 22L, 11L, 50);
        final IndexWithBloat theSame = IndexWithBloat.of("t1", "i1", 100L, 60L, 60); // different size!
        final IndexWithBloat second = IndexWithBloat.of("t2", "i2", 30L, 3L, 10);
        final IndexWithBloat third = IndexWithBloat.of("t3", "i3", 22L, 11L, 50);

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
