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

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionCredentialsTest {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost/postgres";

    @Test
    void getters() {
        final ConnectionCredentials credentials = ConnectionCredentials.ofUrl(DEFAULT_URL, "user", "pswrd");
        assertEquals("user", credentials.getUserName());
        assertEquals("pswrd", credentials.getPassword());
        assertThat(credentials.getConnectionUrls(), hasSize(1));
        assertThat(credentials.getConnectionUrls(), contains(DEFAULT_URL));
    }

    @Test
    void shouldDeduplicateUrls() {
        final ConnectionCredentials credentials = ConnectionCredentials.of(Arrays.asList(DEFAULT_URL, DEFAULT_URL, DEFAULT_URL), "user", "pswrd");
        assertThat(credentials.getConnectionUrls(), hasSize(1));
        assertThat(credentials.getConnectionUrls(), contains(DEFAULT_URL));
    }

    @Test
    void testEqualsAndHashCode() {
        final ConnectionCredentials first = ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "p");
        final ConnectionCredentials second = ConnectionCredentials.ofUrl(DEFAULT_URL, "usr", "p");
        final ConnectionCredentials third = ConnectionCredentials.of(Arrays.asList(DEFAULT_URL, "jdbc:postgresql://host1:5432/postgres"), "u", "p");
        final ConnectionCredentials fourth = ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "pswd");

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "p"));

        // others
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(second, third);
        assertNotEquals(second.hashCode(), third.hashCode());

        assertNotEquals(first, fourth);
        assertNotEquals(first.hashCode(), fourth.hashCode());
    }

    @Test
    void testToString() {
        ConnectionCredentials credentials = ConnectionCredentials.ofUrl(DEFAULT_URL, "user", "pswrd");
        assertEquals("ConnectionCredentials{connectionUrls=[jdbc:postgresql://localhost/postgres], userName='user', password='pswrd'}",
                credentials.toString());

        credentials = ConnectionCredentials.of(Arrays.asList(DEFAULT_URL, "jdbc:postgresql://host1:5432/postgres"), "user", "pswrd");
        assertEquals("ConnectionCredentials{connectionUrls=[jdbc:postgresql://host1:5432/postgres, jdbc:postgresql://localhost/postgres], userName='user', password='pswrd'}",
                credentials.toString());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> ConnectionCredentials.ofUrl(null, null, null));
        assertThrows(NullPointerException.class, () -> ConnectionCredentials.ofUrl(DEFAULT_URL, null, null));
        assertThrows(NullPointerException.class, () -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", null));
        assertThrows(IllegalArgumentException.class, () -> ConnectionCredentials.ofUrl("", "u", "p"));
        assertThrows(IllegalArgumentException.class, () -> ConnectionCredentials.ofUrl("  ", "u", "p"));
        assertThrows(IllegalArgumentException.class, () -> ConnectionCredentials.ofUrl("url", "u", "p"));
        assertThrows(IllegalArgumentException.class, () -> ConnectionCredentials.ofUrl(DEFAULT_URL, "", "p"));
        assertThrows(IllegalArgumentException.class, () -> ConnectionCredentials.ofUrl(DEFAULT_URL, "  ", "p"));
        assertThrows(IllegalArgumentException.class, () -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", ""));
        assertThrows(IllegalArgumentException.class, () -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "  "));

        assertThrows(NullPointerException.class, () -> ConnectionCredentials.of(null, null, null));
        assertThrows(NullPointerException.class, () -> ConnectionCredentials.of(Collections.singleton(DEFAULT_URL), null, null));
        assertThrows(NullPointerException.class, () -> ConnectionCredentials.of(Collections.singleton(DEFAULT_URL), "u", null));
        assertThrows(IllegalArgumentException.class, () -> ConnectionCredentials.of(Collections.emptyList(), "u", "p"));
    }
}
