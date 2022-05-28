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

import io.github.mfvanek.pg.model.table.Column;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexWithNullsTest {

    @Test
    void gettersShouldWork() {
        final IndexWithNulls index = IndexWithNulls.of("t", "i", 11L, "f");
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getIndexName()).isEqualTo("i");
        assertThat(index.getIndexSizeInBytes()).isEqualTo(11L);
        assertThat(index.getNullableColumn()).isEqualTo(Column.ofNullable("t", "f"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> IndexWithNulls.of(null, null, 0, "f"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> IndexWithNulls.of("", null, 0, "f"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> IndexWithNulls.of("  ", null, 0, "f"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> IndexWithNulls.of("t", null, 0, "f"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("indexName cannot be null");
        assertThatThrownBy(() -> IndexWithNulls.of("t", "", 0, "f"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("indexName cannot be blank");
        assertThatThrownBy(() -> IndexWithNulls.of("t", "i", 0, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("columnName cannot be null");
        assertThatThrownBy(() -> IndexWithNulls.of("t", "i", 0, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("columnName cannot be blank");
        assertThatThrownBy(() -> IndexWithNulls.of("t", "i", 0, "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("columnName cannot be blank");
    }

    @Test
    void tableShouldBeTheSame() {
        assertThatThrownBy(() -> invokePrivateConstructor("t", "i", 1L, Column.ofNullable("t2", "f")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name is not the same within given rows");
    }

    @Test
    void testToString() {
        final IndexWithNulls index = IndexWithNulls.of("t", "i", 22L, "f");
        assertThat(index)
                .hasToString("IndexWithNulls{tableName='t', indexName='i', " + "indexSizeInBytes=22, nullableColumn=Column{tableName='t', columnName='f', notNull=false}}");
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
                .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
                .isNotEqualTo(second)
                .doesNotHaveSameHashCodeAs(second);

        // another
        final Index anotherType = Index.of("t1", "i1");
        assertThat(anotherType)
                .isEqualTo(first)
                .hasSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(IndexWithNulls.class)
                .withIgnoredFields("indexSizeInBytes", "nullableColumn")
                .verify();
    }

    @SuppressWarnings("unchecked")
    private static void invokePrivateConstructor(Object ... initargs) throws Throwable {
        final Constructor<IndexWithNulls> constructor = (Constructor<IndexWithNulls>) IndexWithNulls.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        try {
            constructor.newInstance(initargs);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
