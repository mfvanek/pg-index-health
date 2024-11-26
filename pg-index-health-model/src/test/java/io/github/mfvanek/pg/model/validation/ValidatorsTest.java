/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.validation;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidatorsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(Validators.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void valueIsPositive() {
        assertThat(Validators.valueIsPositive(1L, "arg"))
            .isEqualTo(1L);
        assertThatThrownBy(() -> Validators.valueIsPositive(0, "arg"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("arg should be greater than zero");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void indexNameNotBlank() {
        assertThat(Validators.indexNameNotBlank("idx"))
            .isEqualTo("idx");
        assertThatThrownBy(() -> Validators.indexNameNotBlank(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("indexName cannot be null");
        assertThatThrownBy(() -> Validators.indexNameNotBlank(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexName cannot be blank");
    }

    @Test
    void validPercentTest() {
        assertThat(Validators.validPercent(50.0, "remainingPercentageThreshold"))
            .isEqualTo(50.0);

        assertThat(Validators.validPercent(0.0, "remainingPercentageThreshold"))
            .isZero();

        assertThat(Validators.validPercent(100.0, "remainingPercentageThreshold"))
            .isEqualTo(100.0);

        assertThatThrownBy(() -> Validators.validPercent(-0.1, "remainingPercentageThreshold"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("remainingPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");

        assertThatThrownBy(() -> Validators.validPercent(100.1, "remainingPercentageThreshold"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("remainingPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
    }

    @Test
    void argumentNotNegative() {
        assertThatThrownBy(() -> Validators.argumentNotNegative(-1, "arg"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("arg cannot be less than zero");
        assertThat(Validators.argumentNotNegative(0, "arg"))
            .isZero();
        assertThat(Validators.argumentNotNegative(11, "arg"))
            .isEqualTo(11);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void validateThatTableIsTheSameShouldThrowExceptionOnInvalidArgument() {
        assertThatThrownBy(() -> Validators.validateThatTableIsTheSame(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rows cannot be null");

        final List<Table> empty = List.of();
        assertThatThrownBy(() -> Validators.validateThatTableIsTheSame(empty))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rows cannot be empty");

        final List<Table> first = List.of(Table.of("t1", 1L));
        assertThatThrownBy(() -> Validators.validateThatTableIsTheSame(first))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rows should contains at least two items");

        final List<Table> second = List.of(Table.of("t1", 1L), Table.of("t2", 1L));
        assertThatThrownBy(() -> Validators.validateThatTableIsTheSame(second))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name is not the same within given rows");
    }

    @Test
    void validateThatTableIsTheSameShouldWork() {
        final List<Table> rows = List.of(Table.of("t1", 1L), Table.of("t1", 1L));
        assertThatCode(() -> Validators.validateThatTableIsTheSame(rows))
            .doesNotThrowAnyException();
    }
}
