/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.host;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class PgHostImplTest {

    @Test
    void ofUrl() {
        assertThatThrownBy(() -> PgHostImpl.ofUrl("jdbc:postgresql://host-4:6432,host-2:6432,host-3:6432,host-1:6432/db_name?ssl=true&sslmode=require"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl couldn't contain multiple hosts");
    }

    @Test
    void ofUrlWithMultipleHosts() {
        assertThatThrownBy(() -> PgHostImpl.ofUrl("jdbc:postgresql://host-4:6432,host-2:6432,host-3:6432,host-1:6432/db_name?ssl=true&sslmode=require"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl couldn't contain multiple hosts");
    }

    @Test
    void ofSecondaryUrl() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://host-3:6432/db_name?ssl=true&sslmode=require&targetServerType=secondary");
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("host-3");
        assertThat(host.getPort()).isEqualTo(6432);
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://host-3:6432/db_name?ssl=true&sslmode=require&targetServerType=secondary");
        assertThat(host.canBePrimary()).isFalse();
        assertThat(host.cannotBePrimary()).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValues() {
        assertThatThrownBy(() -> PgHostImpl.ofUrl(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgUrl cannot be null");
        assertThatThrownBy(() -> PgHostImpl.ofUrl(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl cannot be blank");
        assertThatThrownBy(() -> PgHostImpl.ofUrl("host"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl has invalid format");
        assertThatThrownBy(() -> PgHostImpl.ofUrl("jdbc:postgresql://:6432"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("hostName cannot be blank");
        assertThatThrownBy(() -> PgHostImpl.ofUrl("jdbc:postgresql://localhost:1023"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("the port number must be in the range from 1024 to 65535");
        assertThatThrownBy(() -> PgHostImpl.ofUrl("jdbc:postgresql://localhost:65536"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("the port number must be in the range from 1024 to 65535");
    }

    @Test
    void ofUncompletedUrl() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432");
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("host-1");
        assertThat(host.getPort()).isEqualTo(6432);
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://host-1:6432");
    }

    @Test
    void toStringTest() {
        assertThat(PgHostImpl.ofUrl("jdbc:postgresql://primary:5432"))
            .hasToString("PgHostImpl{pgUrl='jdbc:postgresql://primary:5432', hostName=primary, port=5432, maybePrimary=true}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final PgHost first = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432/db_name?ssl=true&sslmode=require");
        final PgHost theSame = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432/db_name?ssl=true&sslmode=require");
        final PgHost second = PgHostImpl.ofUrl("jdbc:postgresql://primary:5432");

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        // self
        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // others
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        // another implementation of PgHost
        final PgHost pgHostMock = Mockito.mock(PgHost.class);
        Mockito.when(pgHostMock.canBePrimary()).thenReturn(Boolean.TRUE);
        Mockito.when(pgHostMock.getName()).thenReturn("primary");
        Mockito.when(pgHostMock.getPgUrl()).thenReturn("jdbc:postgresql://primary:5432");
        assertThat(pgHostMock)
            .isNotEqualTo(second)
            .satisfies(h -> {
                assertThat(h.canBePrimary()).isEqualTo(second.canBePrimary());
                assertThat(h.getName()).isEqualTo(second.getName());
                assertThat(h.getPgUrl()).isEqualTo(second.getPgUrl());
            });
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(PgHostImpl.class)
            .withIgnoredFields("pgUrl", "maybePrimary")
            .verify();
    }
}
