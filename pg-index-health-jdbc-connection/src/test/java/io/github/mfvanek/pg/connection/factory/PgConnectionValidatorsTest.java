/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
