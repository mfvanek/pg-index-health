/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.v4.postgres.tc.url;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JsonDeserializerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deserializeShouldWork() throws JsonProcessingException {
        assertThat(objectMapper.getRegisteredModuleIds())
            .hasSizeGreaterThan(1)
            .contains("PgIndexHealthModelModule");

        final ForeignKey original = ForeignKey.ofNotNullColumn("users", "fk_user_role", "role_id");
        final String json = objectMapper.writeValueAsString(original);
        assertThat(json)
            .isEqualTo("""
                {"constraint":{"tableName":"users","constraintName":"fk_user_role","constraintType":"FOREIGN_KEY"},\
                "columns":[{"tableName":"users","columnName":"role_id","notNull":true}]}""");
        final ForeignKey restored = objectMapper.readValue(json, ForeignKey.class);
        assertThat(restored)
            .isEqualTo(original);
    }
}
