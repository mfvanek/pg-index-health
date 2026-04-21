/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.context;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.context.PgContext;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link PgContext} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class PgContextSerializer extends JsonSerializer<PgContext> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final PgContext value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(PgContext.SCHEMA_NAME_FIELD, value.getSchemaName());
        gen.writeNumberField(PgContext.BLOAT_PERCENTAGE_THRESHOLD_FIELD, value.getBloatPercentageThreshold());
        gen.writeNumberField(PgContext.REMAINING_PERCENTAGE_THRESHOLD_FIELD, value.getRemainingPercentageThreshold());
        gen.writeEndObject();
    }
}
