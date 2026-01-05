/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import io.github.mfvanek.pg.core.utils.QueryExecutors;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class StandardCheckInfoTest {

    @Test
    void acrossClusterCheckCannotBeStatic() {
        assertThatThrownBy(() -> new StandardCheckInfo("c", ExecutionTopology.ACROSS_CLUSTER, "sql", QueryExecutors::executeQueryWithSchema, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Runtime check is required for across cluster execution");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldThrowExceptionWhenPassedInvalidData() {
        assertThatThrownBy(() -> new StandardCheckInfo(null, null, null, null, false))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("checkName cannot be null");
        assertThatThrownBy(() -> new StandardCheckInfo("", null, null, null, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("checkName cannot be blank");
        assertThatThrownBy(() -> new StandardCheckInfo("CHECK", null, null, null, false))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("executionTopology cannot be null");
        assertThatThrownBy(() -> new StandardCheckInfo("CHECK", ExecutionTopology.ON_PRIMARY, null, null, false))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("sqlQuery cannot be null");
        assertThatThrownBy(() -> new StandardCheckInfo("CHECK", ExecutionTopology.ON_PRIMARY, "", null, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sqlQuery cannot be blank");
        assertThatThrownBy(() -> new StandardCheckInfo("CHECK", ExecutionTopology.ON_PRIMARY, "select", null, false))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("queryExecutor cannot be null");
    }

    @Test
    void checkNameAlwaysUpperCase() {
        final CheckInfo checkInfo = StandardCheckInfo.ofStatic("custom_check", "select version();");
        assertThat(checkInfo.getName())
            .isEqualTo("CUSTOM_CHECK");
        assertThat(checkInfo.getSqlQuery())
            .isEqualTo("select version();");
        assertThat(checkInfo.isStatic())
            .isTrue();
        assertThat(checkInfo.isAcrossCluster())
            .isFalse();
        assertThat(checkInfo.getQueryExecutor())
            .isNotNull();
    }

    @Test
    void shouldBeAcrossCluster() {
        final CheckInfo checkInfo = StandardCheckInfo.ofCluster("DUPLICATED_INDEXES");
        assertThat(checkInfo.getName())
            .isEqualTo("DUPLICATED_INDEXES");
        assertThat(checkInfo.isRuntime())
            .isTrue();
        assertThat(checkInfo.isAcrossCluster())
            .isTrue();
    }

    @Test
    void shouldBeRuntime() {
        final CheckInfo first = StandardCheckInfo.ofBloat("DUPLICATED_INDEXES");
        assertThat(first.getName())
            .isEqualTo("DUPLICATED_INDEXES");
        assertThat(first.isRuntime())
            .isTrue();
        assertThat(first.isAcrossCluster())
            .isFalse();

        final CheckInfo second = StandardCheckInfo.ofRemainingPercentage("DUPLICATED_INDEXES");
        assertThat(second.getName())
            .isEqualTo("DUPLICATED_INDEXES");
        assertThat(second.isRuntime())
            .isTrue();
        assertThat(second.isAcrossCluster())
            .isFalse();
    }
}
