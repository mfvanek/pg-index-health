/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.dbobject;

import io.github.mfvanek.pg.model.dbobject.AnyObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class AnyObjectSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final AnyObject original = AnyObject.ofType("demo.orders", PgObjectType.TABLE);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"objectName\":\"demo.orders\",\"objectType\":\"table\"}");
        final AnyObject restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), AnyObject.class);
        assertThat(restored)
            .isEqualTo(original);
    }
}
