/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.host;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class PgUrlValidatorsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(PgUrlValidators.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void portInAcceptableRange() {
        assertThatThrownBy(() -> PgUrlValidators.portInAcceptableRange(1023))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("the port number must be in the range from 1024 to 65535");
        assertThatThrownBy(() -> PgUrlValidators.portInAcceptableRange(65_536))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("the port number must be in the range from 1024 to 65535");
        assertThat(PgUrlValidators.portInAcceptableRange(1024))
            .isEqualTo(1024);
        assertThat(PgUrlValidators.portInAcceptableRange(65_535))
            .isEqualTo(65_535);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void hostNameNotBlank() {
        assertThatThrownBy(() -> PgUrlValidators.hostNameNotBlank(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("hostName cannot be null");
        assertThatThrownBy(() -> PgUrlValidators.hostNameNotBlank(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("hostName cannot be blank");
        assertThatThrownBy(() -> PgUrlValidators.hostNameNotBlank("  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("hostName cannot be blank");
        assertThat(PgUrlValidators.hostNameNotBlank("localhost"))
            .isEqualTo("localhost");
    }
}
