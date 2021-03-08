/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import org.junit.jupiter.api.Test;

import static io.github.mfvanek.pg.utils.NamedParametersParser.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NamedParametersParserTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> parse(null));
        assertThrows(IllegalArgumentException.class, () -> parse(""));
        assertThrows(IllegalArgumentException.class, () -> parse("   "));
    }

    @Test
    void parseWithoutNamedParams() {
        assertEquals("select * from orders;",
                parse("select * from orders;"));
        assertEquals("select * from accounts where account_number = ?",
                parse("select * from accounts where account_number = ?"));
    }

    @Test
    void parseWithSingleLineComment() {
        assertEquals("select * from accounts --test",
                parse("select * from accounts --test"));
        assertEquals("select * from accounts where account_number = ? --test",
                parse("select * from accounts where account_number = ? --test"));
        assertEquals("select * from clients where id = ? --test\n and name = ?",
                parse("select * from clients where id = :p_id --test\n and name = :p_name"));
    }

    @Test
    void parseWithMultiLineComment() {
        assertEquals("select * from /* test \ncomment */ accounts",
                parse("select * from /* test \ncomment */ accounts"));
        assertEquals("select * from /* test \n:comment */ accounts where id = ?;",
                parse("select * from /* test \n:comment */ accounts where id = :p_id;"));
    }

    @Test
    void parseWithQuotes() {
        assertEquals("select id as \"counter\" from orders where name = 'test';",
                parse("select id as \"counter\" from orders where name = 'test';"));
        assertEquals("select id as \"counter\" from orders where name = ?;",
                parse("select id as \"counter\" from orders where name = :p_name;"));
    }

    @Test
    void parseWithDoubleColon() {
        assertEquals("select id::text from orders where id < 10::bigint",
                parse("select id::text from orders where id < 10::bigint"));
        assertEquals("select * from accounts where account_number = ?::text",
                parse("select * from accounts where account_number = ?::text"));
        assertEquals("select * from accounts where account_number = ?::text",
                parse("select * from accounts where account_number = :p_num::text"));
        assertEquals("select c.* from clients c where c.first_name = ?::text and c.last_name = ?::text;",
                parse("select c.* from clients c where c.first_name = :p_name::text and c.last_name = :p_surname::text;"));
    }

    @Test
    void parseWithInvalidNameAfterDoubleColon() {
        assertEquals("select * from accounts where account_number = ?::?",
                parse("select * from accounts where account_number = :p_num:::text"));
    }

    @Test
    void parseWithInvalidNameAfterColon() {
        assertEquals("select * from accounts where account_number = ?%num::text",
                parse("select * from accounts where account_number = :p%num::text"));
    }

    @Test
    void parseWithColonInTheEnd() {
        assertEquals("select * from accounts where account_number = :",
                parse("select * from accounts where account_number = :"));

        assertEquals("select * from accounts where account_number = :%",
                parse("select * from accounts where account_number = :%"));
    }
}
