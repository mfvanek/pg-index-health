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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.function.StoredFunction;

import java.io.IOException;

/**
 * A deserializer for {@link StoredFunction} objects, enabling JSON deserialization into immutable {@code StoredFunction} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class StoredFunctionDeserializer extends JsonDeserializer<StoredFunction> {

    /**
     * {@inheritDoc}
     */
    @Override
    public StoredFunction deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String functionName = node.get(StoredFunction.FUNCTION_NAME_FIELD).asText();
        final String functionSignature = node.get(StoredFunction.FUNCTION_SIGNATURE_FIELD).asText();
        return StoredFunction.of(functionName, functionSignature);
    }
}
