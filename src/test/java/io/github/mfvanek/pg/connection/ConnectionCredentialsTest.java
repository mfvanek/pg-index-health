/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConnectionCredentialsTest {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost/postgres";

    @Test
    void getters() {
        final ConnectionCredentials credentials = ConnectionCredentials.ofUrl(DEFAULT_URL, "user", "pswrd");
        assertThat(credentials.getUserName()).isEqualTo("user");
        assertThat(credentials.getPassword()).isEqualTo("pswrd");
        assertThat(credentials.getConnectionUrls()).hasSize(1);
        assertThat(credentials.getConnectionUrls()).contains(DEFAULT_URL);
    }

    @Test
    void shouldDeduplicateUrls() {
        final ConnectionCredentials credentials = ConnectionCredentials.of(Arrays.asList(DEFAULT_URL, DEFAULT_URL, DEFAULT_URL), "user", "pswrd");
        assertThat(credentials.getConnectionUrls()).hasSize(1);
        assertThat(credentials.getConnectionUrls()).contains(DEFAULT_URL);
    }

    @Test
    void shouldCreateDefensiveCopyOfUrlsList() {
        final List<String> urls = new ArrayList<>(Arrays.asList(
                "jdbc:postgresql://localhost/first",
                "jdbc:postgresql://localhost/second",
                "jdbc:postgresql://localhost/third"));
        final ConnectionCredentials credentials = ConnectionCredentials.of(urls, "user", "password");

        urls.add("jdbc:postgresql://localhost/fourth");

        assertThat(credentials.getConnectionUrls()).hasSize(3);
        assertThat(credentials.getConnectionUrls()).doesNotContain("jdbc:postgresql://localhost/fourth");
        assertThatThrownBy(() -> credentials.getConnectionUrls().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testEqualsAndHashCode() {
        final ConnectionCredentials first = ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "p");
        final ConnectionCredentials second = ConnectionCredentials.ofUrl(DEFAULT_URL, "usr", "p");
        final ConnectionCredentials third = ConnectionCredentials.of(Arrays.asList(DEFAULT_URL, "jdbc:postgresql://host1:5432/postgres"), "u", "p");
        final ConnectionCredentials fourth = ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "pswd");

        // null
        assertThat(first).isNotNull();
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(first).isNotEqualTo(BigDecimal.ZERO);

        // self
        assertThat(first).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(first.hashCode());

        // the same
        assertThat(ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "p")).isEqualTo(first);

        // others
        assertThat(second).isNotEqualTo(first);
        assertThat(second.hashCode()).isNotEqualTo(first.hashCode());

        assertThat(third).isNotEqualTo(first);
        assertThat(third.hashCode()).isNotEqualTo(first.hashCode());

        assertThat(third).isNotEqualTo(second);
        assertThat(third.hashCode()).isNotEqualTo(second.hashCode());

        assertThat(fourth).isNotEqualTo(first);
        assertThat(fourth.hashCode()).isNotEqualTo(first.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ConnectionCredentials.class)
                .verify();
    }

    @Test
    void testToString() {
        ConnectionCredentials credentials = ConnectionCredentials.ofUrl(DEFAULT_URL, "user", "pswrd");
        assertThat(credentials.toString()).isEqualTo("ConnectionCredentials{connectionUrls=[jdbc:postgresql://localhost/postgres], userName='user', password='pswrd'}");

        credentials = ConnectionCredentials.of(Arrays.asList(DEFAULT_URL, "jdbc:postgresql://host1:5432/postgres"), "user", "pswrd");
        assertThat(credentials.toString()).isEqualTo(
                "ConnectionCredentials{connectionUrls=[jdbc:postgresql://host1:5432/postgres, jdbc:postgresql://localhost/postgres], userName='user', password='pswrd'}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(null, null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl("", "u", "p")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl("  ", "u", "p")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl("url", "u", "p")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "", "p")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "  ", "p")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "  ")).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ConnectionCredentials.of(null, null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectionCredentials.of(Collections.singleton(DEFAULT_URL), null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectionCredentials.of(Collections.singleton(DEFAULT_URL), "u", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ConnectionCredentials.of(Collections.emptyList(), "u", "p")).isInstanceOf(IllegalArgumentException.class);
    }
}
