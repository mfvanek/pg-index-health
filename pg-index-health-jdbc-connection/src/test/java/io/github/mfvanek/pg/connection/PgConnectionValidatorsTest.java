/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class PgConnectionValidatorsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(PgConnectionValidators.class))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void connectionUrlsNotEmptyAndValid() {
        final List<String> urls = List.of("jdbc:postgresql:/", "jdb:postgresl://");
        assertThatThrownBy(() -> PgConnectionValidators.connectionUrlsNotEmptyAndValid(urls))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("connectionUrl has invalid format");
    }

    @Test
    void portInAcceptableRange() {
        assertThatThrownBy(() -> PgConnectionValidators.portInAcceptableRange(1023))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("the port number must be in the range from 1024 to 65535");
        assertThatThrownBy(() -> PgConnectionValidators.portInAcceptableRange(65_536))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("the port number must be in the range from 1024 to 65535");
        assertThat(PgConnectionValidators.portInAcceptableRange(1024))
                .isEqualTo(1024);
        assertThat(PgConnectionValidators.portInAcceptableRange(65_535))
                .isEqualTo(65_535);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void hostNameNotBlank() {
        assertThatThrownBy(() -> PgConnectionValidators.hostNameNotBlank(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("hostName cannot be null");
        assertThatThrownBy(() -> PgConnectionValidators.hostNameNotBlank(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("hostName cannot be blank or empty");
        assertThatThrownBy(() -> PgConnectionValidators.hostNameNotBlank("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("hostName cannot be blank or empty");
        assertThat(PgConnectionValidators.hostNameNotBlank("localhost"))
                .isEqualTo("localhost");
    }
}
