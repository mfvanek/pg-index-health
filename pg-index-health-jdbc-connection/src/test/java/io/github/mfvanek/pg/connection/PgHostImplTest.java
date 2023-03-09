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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class PgHostImplTest {

    @Test
    void ofPrimary() {
        final PgHost host = PgHostImpl.ofPrimary();
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("primary");
        assertThat(host.getPort()).isEqualTo(5432);
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://primary");
        assertThat(host.canBePrimary()).isTrue();
        assertThat(host.cannotBePrimary()).isFalse();
    }

    @Test
    void ofPrimaryWithPort() {
        final PgHost host = PgHostImpl.ofPrimary(6432);
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("primary");
        assertThat(host.getPort()).isEqualTo(6432);
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://primary");
        assertThat(host.canBePrimary()).isTrue();
        assertThat(host.cannotBePrimary()).isFalse();
    }

    @Test
    void ofName() {
        final PgHost host = PgHostImpl.ofName("any-host");
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("any-host");
        assertThat(host.getPort()).isEqualTo(5432);
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://any-host");
        assertThat(host.canBePrimary()).isTrue();
        assertThat(host.cannotBePrimary()).isFalse();
    }

    @Test
    void ofNameWithPort() {
        final PgHost host = PgHostImpl.ofName("any-host", 6432);
        assertThat(host).isNotNull();
        assertThat(host.getName()).isEqualTo("any-host");
        assertThat(host.getPort()).isEqualTo(6432);
        assertThat(host.getPgUrl()).isEqualTo("jdbc:postgresql://any-host");
        assertThat(host.canBePrimary()).isTrue();
        assertThat(host.cannotBePrimary()).isFalse();
    }

    @Test
    void withPort() {
        final PgHost hostWithLowerBoundPort = PgHostImpl.ofName("any-host", 1);
        final PgHost hostWithUpperBoundPort = PgHostImpl.ofName("any-host", 65_535);
        assertThat(hostWithLowerBoundPort.getPort()).isEqualTo(1);
        assertThat(hostWithUpperBoundPort.getPort()).isEqualTo(65_535);
    }

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
        assertThatThrownBy(() -> PgHostImpl.ofName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("hostName cannot be null");
        assertThatThrownBy(() -> PgHostImpl.ofName("hostname", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("the port number must be in the range from 1 to 65535");
        assertThatThrownBy(() -> PgHostImpl.ofName("hostname", 65_536))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("the port number must be in the range from 1 to 65535");
        assertThatThrownBy(() -> PgHostImpl.ofUrl(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("pgUrl cannot be null");
        assertThatThrownBy(() -> PgHostImpl.ofUrl(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pgUrl cannot be blank or empty");
        assertThatThrownBy(() -> PgHostImpl.ofUrl("host"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pgUrl has invalid format");
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
        assertThat(PgHostImpl.ofPrimary())
                .hasToString("PgHostImpl{pgUrl='jdbc:postgresql://primary', hostName=primary, port=5432, maybePrimary=true}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final PgHost first = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432/db_name?ssl=true&sslmode=require");
        final PgHost theSame = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432/db_name?ssl=true&sslmode=require");
        final PgHost second = PgHostImpl.ofPrimary();

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
        Mockito.when(pgHostMock.canBePrimary()).thenReturn(true);
        Mockito.when(pgHostMock.getName()).thenReturn("primary");
        Mockito.when(pgHostMock.getPgUrl()).thenReturn("jdbc:postgresql://primary");
        assertThat(pgHostMock).isNotEqualTo(second);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(PgHostImpl.class)
                .withIgnoredFields("pgUrl", "maybePrimary")
                .verify();
    }
}
