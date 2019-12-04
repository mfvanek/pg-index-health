/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgHostImplTest {

    @Test
    void ofMaster() {
        final var host = PgHostImpl.ofMaster();
        assertNotNull(host);
        assertEquals("master", host.getName());
        assertEquals("jdbc:postgresql://master", host.getPgUrl());
    }

    @Test
    void ofName() {
        final var host = PgHostImpl.ofName("any-host");
        assertNotNull(host);
        assertEquals("any-host", host.getName());
        assertEquals("jdbc:postgresql://any-host", host.getPgUrl());

        assertThrows(NullPointerException.class, () -> PgHostImpl.ofName(null));
    }

    @Test
    void ofUrl() {
        final var host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertNotNull(host);
        assertEquals("One of [host-3, host-4, host-1, host-2]", host.getName());
        assertEquals("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require", host.getPgUrl());

        assertThrows(NullPointerException.class, () -> PgHostImpl.ofUrl(null));
        assertThrows(IllegalArgumentException.class, () -> PgHostImpl.ofUrl(""));
        assertThrows(IllegalArgumentException.class, () -> PgHostImpl.ofUrl("host"));
    }

    @Test
    void ofUncompletedUrl() {
        final var host = PgHostImpl.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432");
        assertNotNull(host);
        assertEquals("One of [host-3, host-4, host-1, host-2]", host.getName());
        assertEquals("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432", host.getPgUrl());
    }
}
