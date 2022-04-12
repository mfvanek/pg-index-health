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
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgHostImplTest {

    @Test
    void ofPrimary() {
        final PgHost host = PgHostImpl.ofPrimary();
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("primary");
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://primary");
        assertThat(host.canBePrimary()).isTrue();
        assertThat(host.cannotBePrimary()).isFalse();
    }

    @Test
    void ofName() {
        final PgHost host = PgHostImpl.ofName("any-host");
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("any-host");
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://any-host");
        assertThat(host.canBePrimary()).isTrue();
        assertThat(host.cannotBePrimary()).isFalse();
    }

    @Test
    void ofUrl() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("One of [host-3, host-4, host-1, host-2]");
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertThat(host.canBePrimary()).isTrue();
        assertThat(host.cannotBePrimary()).isFalse();
    }

    @Test
    void ofSecondaryUrl() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require&targetServerType=secondary");
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("One of [host-3, host-4, host-1, host-2]");
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require&targetServerType=secondary");
        assertThat(host.canBePrimary()).isFalse();
        assertThat(host.cannotBePrimary()).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValues() {
        assertThatThrownBy(() -> PgHostImpl.ofName(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> PgHostImpl.ofUrl(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> PgHostImpl.ofUrl("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PgHostImpl.ofUrl("host")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ofUncompletedUrl() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432");
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("One of [host-3, host-4, host-1, host-2]");
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432");
    }

    @Test
    void toStringTest() {
        final PgHost host = PgHostImpl.ofPrimary();
        assertThat(host).isNotNull();
        assertThat(host.toString()).isEqualTo("PgHostImpl{pgUrl='jdbc:postgresql://primary', hostNames=[primary], maybePrimary=true}");
    }

    @Test
    void equalsAndHashCode() {
        final PgHost first = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        final PgHost theSame = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        final PgHost withDifferentHostsOrder = PgHostImpl.ofUrl("jdbc:postgresql://host-2:5432,host-1:4432,host-4:3432,host-3:2432/db_name?ssl=true&sslmode=require");
        final PgHost second = PgHostImpl.ofPrimary();

        assertThat(first).isNotNull();
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(BigDecimal.ZERO).isNotEqualTo(first);

        // self
        assertThat(first).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(first.hashCode());

        // the same
        assertThat(theSame).isEqualTo(first);
        assertThat(theSame.hashCode()).isEqualTo(first.hashCode());

        // others
        assertThat(withDifferentHostsOrder).isEqualTo(first);
        assertThat(withDifferentHostsOrder.hashCode()).isEqualTo(first.hashCode());

        assertThat(second).isNotEqualTo(first);
        assertThat(second.hashCode()).isNotEqualTo(first.hashCode());

        // another implementation of PgHost
        final PgHost pgHostMock = Mockito.mock(PgHost.class);
        Mockito.when(pgHostMock.canBePrimary()).thenReturn(true);
        Mockito.when(pgHostMock.getName()).thenReturn("primary");
        Mockito.when(pgHostMock.getPgUrl()).thenReturn("jdbc:postgresql://primary");
        assertThat(pgHostMock).isNotEqualTo(second);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(PgHostImpl.class)
                .withIgnoredFields("pgUrl", "maybePrimary")
                .verify();
    }
}
