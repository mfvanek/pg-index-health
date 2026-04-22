/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.function;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.function.StoredFunction;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link StoredFunction} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class StoredFunctionSerializer extends ValueSerializer<StoredFunction> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final StoredFunction value, final JsonGenerator gen, final SerializerProvider serializers) {
        gen.writeStartObject();
        gen.writeStringProperty(StoredFunction.FUNCTION_NAME_FIELD, value.getFunctionName());
        gen.writeStringProperty(StoredFunction.FUNCTION_SIGNATURE_FIELD, value.getFunctionSignature());
        gen.writeEndObject();
    }
}
