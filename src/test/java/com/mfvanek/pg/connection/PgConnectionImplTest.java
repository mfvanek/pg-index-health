/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
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
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {});

    @Test
    void getMasterDataSource() {
        final var connection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        assertNotNull(connection.getDataSource());
        assertThat(connection.getHost(), equalTo(PgHostImpl.ofMaster()));
    }

    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> PgConnectionImpl.ofMaster(null));
        assertThrows(NullPointerException.class, () -> PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), null));
    }

    @Test
    void equalsAndHashCode() {
        final var first = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        final var theSame = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        final var second = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofName("second"));

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
        final var connection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        assertEquals("PgConnectionImpl{host=PgHostImpl{pgUrl='jdbc:postgresql://master', hostNames=[master]}}",
                connection.toString());
    }
}
