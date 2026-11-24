/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.v4.jackson3;

import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JsonDeserializerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deserializeShouldWork() {
        final PgContext ctx = PgContext.ofDefault();
        assertThat(objectMapper.registeredModules())
            .hasSizeGreaterThan(1)
            .extracting(JacksonModule::getModuleName)
            .contains("PgIndexHealthModelModule");

        final ForeignKey original = ForeignKey.ofBigint(ctx, "users", "fk_user_role", "role_id");
        final String json = objectMapper.writeValueAsString(original);
        assertThat(json)
            .isEqualTo("""
                {"constraint":{"tableName":"users","constraintName":"fk_user_role","constraintType":"FOREIGN_KEY"},\
                "columns":[{"tableName":"users","columnName":"role_id","notNull":true,"columnType":"bigint"}]}""");
        final ForeignKey restored = objectMapper.readValue(json, ForeignKey.class);
        assertThat(restored)
            .isEqualTo(original);
    }
}
