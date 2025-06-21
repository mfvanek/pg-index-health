/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ConnectionCredentialsTest {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost/postgres";

    @Test
    void getters() {
        final ConnectionCredentials credentials = ConnectionCredentials.ofUrl(DEFAULT_URL, "user", "pswrd");
        assertThat(credentials.getUserName()).isEqualTo("user");
        assertThat(credentials.getPassword()).isEqualTo("pswrd");
        assertThat(credentials.getConnectionUrls())
            .hasSize(1)
            .contains(DEFAULT_URL)
            .isUnmodifiable();
    }

    @Test
    void shouldDeduplicateUrls() {
        final ConnectionCredentials credentials = ConnectionCredentials.of(List.of(DEFAULT_URL, DEFAULT_URL, DEFAULT_URL), "user", "pswrd");
        assertThat(credentials.getConnectionUrls())
            .hasSize(1)
            .contains(DEFAULT_URL)
            .isUnmodifiable();
    }

    @Test
    void shouldCreateDefensiveCopyOfUrlsList() {
        final List<String> urls = new ArrayList<>(List.of(
            "jdbc:postgresql://localhost/first",
            "jdbc:postgresql://localhost/second",
            "jdbc:postgresql://localhost/third"));
        final ConnectionCredentials credentials = ConnectionCredentials.of(urls, "user", "password");

        urls.add("jdbc:postgresql://localhost/fourth");

        assertThat(credentials.getConnectionUrls())
            .hasSize(3)
            .doesNotContain("jdbc:postgresql://localhost/fourth")
            .isUnmodifiable();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final ConnectionCredentials first = ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "p");
        final ConnectionCredentials second = ConnectionCredentials.ofUrl(DEFAULT_URL, "usr", "p");
        final ConnectionCredentials third = ConnectionCredentials.of(List.of(DEFAULT_URL, "jdbc:postgresql://host1:5432/postgres"), "u", "p");
        final ConnectionCredentials fourth = ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "pswd");

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        // self
        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the same
        assertThat(ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "p"))
            .isEqualTo(first);

        // others
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first)
            .isNotEqualTo(second)
            .doesNotHaveSameHashCodeAs(second);

        assertThat(fourth)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ConnectionCredentials.class)
            .verify();
    }

    @Test
    void testToString() {
        ConnectionCredentials credentials = ConnectionCredentials.ofUrl(DEFAULT_URL, "user", "pswrd");
        assertThat(credentials)
            .hasToString("ConnectionCredentials{connectionUrls=[jdbc:postgresql://localhost/postgres], userName='user', password='pswrd'}");

        credentials = ConnectionCredentials.of(List.of(DEFAULT_URL, "jdbc:postgresql://host1:5432/postgres"), "user", "pswrd");
        assertThat(credentials)
            .hasToString("ConnectionCredentials{connectionUrls=[jdbc:postgresql://host1:5432/postgres, jdbc:postgresql://localhost/postgres], userName='user', password='pswrd'}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("writeUrl cannot be null");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userName cannot be null");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("password cannot be null");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl("", "u", "p"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("writeUrl cannot be blank");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl("  ", "u", "p"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("writeUrl cannot be blank");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl("url", "u", "p"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("writeUrl has invalid format");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "", "p"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("userName cannot be blank");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "  ", "p"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("userName cannot be blank");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("password cannot be blank");
        assertThatThrownBy(() -> ConnectionCredentials.ofUrl(DEFAULT_URL, "u", "  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("password cannot be blank");

        assertThatThrownBy(() -> ConnectionCredentials.of(null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("connectionUrls cannot be null");
        final Set<String> defaultUrls = Set.of(DEFAULT_URL);
        assertThatThrownBy(() -> ConnectionCredentials.of(defaultUrls, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userName cannot be null");
        assertThatThrownBy(() -> ConnectionCredentials.of(defaultUrls, "u", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("password cannot be null");
        final List<String> emptyList = List.of();
        assertThatThrownBy(() -> ConnectionCredentials.of(emptyList, "u", "p"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("connectionUrls have to contain at least one url");
    }
}
