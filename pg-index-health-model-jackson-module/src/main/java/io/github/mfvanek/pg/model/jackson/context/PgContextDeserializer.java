/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.context;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.jackson.common.AbstractDeserializer;

import java.io.IOException;

/**
 * A deserializer for {@link PgContext} objects, enabling JSON deserialization into immutable {@code PgContext} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class PgContextDeserializer extends AbstractDeserializer<PgContext> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PgContext deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String schemaName = getStringField(ctxt, node, PgContext.SCHEMA_NAME_FIELD);
        final double bloat = getDoubleField(ctxt, node, PgContext.BLOAT_PERCENTAGE_THRESHOLD_FIELD);
        final double remaining = getDoubleField(ctxt, node, PgContext.REMAINING_PERCENTAGE_THRESHOLD_FIELD);
        return PgContext.of(schemaName, bloat, remaining);
    }
}
