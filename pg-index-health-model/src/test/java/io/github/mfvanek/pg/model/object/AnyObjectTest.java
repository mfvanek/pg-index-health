/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.object;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnyObjectTest {

    @Test
    void getObjectNameShouldWork() {
        final AnyObject first = AnyObject.ofType("t", PgObjectType.TABLE);
        assertThat(first.getName())
            .isEqualTo("t");
        assertThat(first.getObjectType())
            .isEqualTo(PgObjectType.TABLE);

        final AnyObject second = AnyObject.ofRaw("v", "View");
        assertThat(second.getName())
            .isEqualTo("v");
        assertThat(second.getObjectType())
            .isEqualTo(PgObjectType.VIEW);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValues() {
        assertThatThrownBy(() -> AnyObject.ofType(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("objectName cannot be null");
        assertThatThrownBy(() -> AnyObject.ofType("", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("objectName cannot be blank");
        assertThatThrownBy(() -> AnyObject.ofType("  ", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("objectName cannot be blank");
        assertThatThrownBy(() -> AnyObject.ofType("t", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("objectType cannot be null");

        assertThatThrownBy(() -> AnyObject.ofRaw("t", "qwerty"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown objectType: qwerty");
    }

    @Test
    void testToString() {
        assertThat(AnyObject.ofRaw("mv", "Materialized View"))
            .hasToString("AnyObject{objectName='mv', objectType=MATERIALIZED_VIEW}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final AnyObject first = AnyObject.ofType("t", PgObjectType.TABLE);
        final AnyObject theSame = AnyObject.ofRaw("t", "table");
        final AnyObject second = AnyObject.ofRaw("v", "View");
        final AnyObject third = AnyObject.ofType("t", PgObjectType.MATERIALIZED_VIEW);

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
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(AnyObject.class)
            .verify();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void compareToTest() {
        final AnyObject first = AnyObject.ofType("t", PgObjectType.TABLE);
        final AnyObject theSame = AnyObject.ofRaw("t", "table");
        final AnyObject second = AnyObject.ofRaw("s", "Table");
        final AnyObject third = AnyObject.ofType("t", PgObjectType.MATERIALIZED_VIEW);

        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("other cannot be null");

        assertThat(first)
            .isEqualByComparingTo(first) // self
            .isEqualByComparingTo(theSame) // the same
            .isGreaterThan(second)
            .isLessThan(third);

        assertThat(second)
            .isLessThan(first)
            .isLessThan(third);

        assertThat(third)
            .isGreaterThan(first)
            .isGreaterThan(second);
    }
}
