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

import io.github.mfvanek.pg.model.function.StoredFunction;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

/**
 * A deserializer for {@link StoredFunction} objects, enabling JSON deserialization into immutable {@code StoredFunction} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class StoredFunctionDeserializer extends ModelDeserializer<StoredFunction> {

    /**
     * {@inheritDoc}
     */
    @Override
    public StoredFunction deserialize(final JsonParser p, final DeserializationContext ctxt) {
        final JsonNode rootNode = ctxt.readTree(p);
        final String functionName = getStringField(ctxt, rootNode, StoredFunction.FUNCTION_NAME_FIELD);
        final String functionSignature = getStringField(ctxt, rootNode, StoredFunction.FUNCTION_SIGNATURE_FIELD);
        return StoredFunction.of(functionName, functionSignature);
    }
}
