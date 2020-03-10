/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgConnectionImplTest {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres =
            PostgresExtensionFactory.database();

    @Test
    void getMasterDataSource() {
        final PgConnection connection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        assertNotNull(connection.getDataSource());
        assertThat(connection.getHost(), equalTo(PgHostImpl.ofMaster()));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> PgConnectionImpl.ofMaster(null));
        assertThrows(NullPointerException.class, () -> PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), null));
    }

    @Test
    void equalsAndHashCode() {
        final PgConnection first = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        final PgConnection theSame = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        final PgConnection second = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofName("second"));

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        assertEquals(first, theSame);
        assertEquals(first.hashCode(), theSame.hashCode());

        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void toStringTest() {
        final PgConnection connection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        assertEquals("PgConnectionImpl{host=PgHostImpl{pgUrl='jdbc:postgresql://master', hostNames=[master]}}",
                connection.toString());
    }
}
