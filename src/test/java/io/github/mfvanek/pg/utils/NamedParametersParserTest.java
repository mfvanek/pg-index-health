/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NamedParametersParserTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> parse(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> parse("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parse("   ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parseWithoutNamedParams() {
        assertThat(parse("select * from orders;"))
                .isEqualTo("select * from orders;");
        assertThat(parse("select * from accounts where account_number = ?"))
                .isEqualTo("select * from accounts where account_number = ?");
    }

    @Test
    void parseWithSingleLineComment() {
        assertThat(parse("select * from accounts --test"))
                .isEqualTo("select * from accounts --test");
        assertThat(parse("select * from accounts where account_number = ? --test"))
                .isEqualTo("select * from accounts where account_number = ? --test");
        assertThat(parse("select * from clients where id = :p_id --test\n and name = :p_name"))
                .isEqualTo("select * from clients where id = ? --test\n and name = ?");
    }

    @Test
    void parseWithMultiLineComment() {
        assertThat(parse("select * from /* test \ncomment */ accounts"))
                .isEqualTo("select * from /* test \ncomment */ accounts");
        assertThat(parse("select * from /* test \n:comment */ accounts where id = :p_id;"))
                .isEqualTo("select * from /* test \n:comment */ accounts where id = ?;");
    }

    @Test
    void parseWithQuotes() {
        assertThat(parse("select id as \"counter\" from orders where name = 'test';"))
                .isEqualTo("select id as \"counter\" from orders where name = 'test';");
        assertThat(parse("select id as \"counter\" from orders where name = :p_name;"))
                .isEqualTo("select id as \"counter\" from orders where name = ?;");
    }

    @Test
    void parseWithDoubleColon() {
        assertThat(parse("select id::text from orders where id < 10::bigint"))
                .isEqualTo("select id::text from orders where id < 10::bigint");
        assertThat(parse("select * from accounts where account_number = ?::text"))
                .isEqualTo("select * from accounts where account_number = ?::text");
        assertThat(parse("select * from accounts where account_number = :p_num::text"))
                .isEqualTo("select * from accounts where account_number = ?::text");
        assertThat(parse("select c.* from clients c where c.first_name = :p_name::text and c.last_name = :p_surname::text;"))
                .isEqualTo("select c.* from clients c where c.first_name = ?::text and c.last_name = ?::text;");
    }

    @Test
    void parseWithInvalidNameAfterDoubleColon() {
        assertThat(parse("select * from accounts where account_number = :p_num:::text"))
                .isEqualTo("select * from accounts where account_number = ?::?");
    }

    @Test
    void parseWithInvalidNameAfterColon() {
        assertThat(parse("select * from accounts where account_number = :p%num::text"))
                .isEqualTo("select * from accounts where account_number = ?%num::text");
    }

    @Test
    void parseWithColonInTheEnd() {
        assertThat(parse("select * from accounts where account_number = :"))
                .isEqualTo("select * from accounts where account_number = :");
        assertThat(parse("select * from accounts where account_number = :%"))
                .isEqualTo("select * from accounts where account_number = :%");
    }
}
