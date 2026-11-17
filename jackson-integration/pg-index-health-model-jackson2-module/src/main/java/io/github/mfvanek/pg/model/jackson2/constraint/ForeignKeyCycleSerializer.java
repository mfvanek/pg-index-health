/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson2.constraint;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.constraint.ForeignKeyCycle;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link ForeignKeyCycle} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.50.0
 */
public class ForeignKeyCycleSerializer extends JsonSerializer<ForeignKeyCycle> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final ForeignKeyCycle value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        serializers.defaultSerializeField(ForeignKeyCycle.FOREIGN_KEYS_FIELD, value.getForeignKeys(), gen);
        gen.writeEndObject();
    }
}
