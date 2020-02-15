/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.utils.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PgConnectionValidatorsTest {

    @Test
    void privateConstructor() {
        assertThrows(UnsupportedOperationException.class, () -> TestUtils.invokePrivateConstructor(PgConnectionValidators.class));
    }
}
