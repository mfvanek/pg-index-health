/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.function;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.function.StoredFunction;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link StoredFunction} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class StoredFunctionSerializer extends JsonSerializer<StoredFunction> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final StoredFunction value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(StoredFunction.FUNCTION_NAME_FIELD, value.getFunctionName());
        gen.writeStringField(StoredFunction.FUNCTION_SIGNATURE_FIELD, value.getFunctionSignature());
        gen.writeEndObject();
    }
}
