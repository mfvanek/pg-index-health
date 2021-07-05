/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PgHostImplTest {

    @Test
    void ofPrimary() {
        final PgHost host = PgHostImpl.ofPrimary();
        assertNotNull(host);
        assertEquals("primary", host.getName());
        assertEquals("jdbc:postgresql://primary", host.getPgUrl());
        assertTrue(host.canBePrimary());
        assertFalse(host.cannotBePrimary());
    }

    @Test
    void ofName() {
        final PgHost host = PgHostImpl.ofName("any-host");
        assertNotNull(host);
        assertEquals("any-host", host.getName());
        assertEquals("jdbc:postgresql://any-host", host.getPgUrl());
        assertTrue(host.canBePrimary());
        assertFalse(host.cannotBePrimary());
    }

    @Test
    void ofUrl() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertNotNull(host);
        assertEquals("One of [host-3, host-4, host-1, host-2]", host.getName());
        assertEquals("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require", host.getPgUrl());
        assertTrue(host.canBePrimary());
        assertFalse(host.cannotBePrimary());
    }

    @Test
    void ofSecondaryUrl() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require&targetServerType=secondary");
        assertNotNull(host);
        assertEquals("One of [host-3, host-4, host-1, host-2]", host.getName());
        assertEquals("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require&targetServerType=secondary",
                host.getPgUrl());
        assertFalse(host.canBePrimary());
        assertTrue(host.cannotBePrimary());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValues() {
        assertThrows(NullPointerException.class, () -> PgHostImpl.ofName(null));
        assertThrows(NullPointerException.class, () -> PgHostImpl.ofUrl(null));
        assertThrows(IllegalArgumentException.class, () -> PgHostImpl.ofUrl(""));
        assertThrows(IllegalArgumentException.class, () -> PgHostImpl.ofUrl("host"));
    }

    @Test
    void ofUncompletedUrl() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432");
        assertNotNull(host);
        assertEquals("One of [host-3, host-4, host-1, host-2]", host.getName());
        assertEquals("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432", host.getPgUrl());
    }

    @Test
    void toStringTest() {
        final PgHost host = PgHostImpl.ofPrimary();
        assertNotNull(host);
        assertEquals("PgHostImpl{pgUrl='jdbc:postgresql://primary', hostNames=[primary], maybePrimary=true}",
                host.toString());
    }

    @Test
    void equalsAndHashCode() {
        final PgHost first = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        final PgHost theSame = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        final PgHost withDifferentHostsOrder = PgHostImpl.ofUrl("jdbc:postgresql://host-2:5432,host-1:4432,host-4:3432,host-3:2432/db_name?ssl=true&sslmode=require");
        final PgHost second = PgHostImpl.ofPrimary();

        assertNotEquals(first, null);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, theSame);
        assertEquals(first.hashCode(), theSame.hashCode());

        // others
        assertEquals(first, withDifferentHostsOrder);
        assertEquals(first.hashCode(), withDifferentHostsOrder.hashCode());

        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        // another implementation of PgHost
        final PgHost pgHostMock = Mockito.mock(PgHost.class);
        Mockito.when(pgHostMock.canBePrimary()).thenReturn(true);
        Mockito.when(pgHostMock.getName()).thenReturn("primary");
        Mockito.when(pgHostMock.getPgUrl()).thenReturn("jdbc:postgresql://primary");
        assertNotEquals(second, pgHostMock);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(PgHostImpl.class)
                .withIgnoredFields("pgUrl", "maybePrimary")
                .verify();
    }
}
