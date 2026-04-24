/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.context;

import io.github.mfvanek.pg.model.context.PgContext;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * A custom JSON serializer for the {@link PgContext} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class PgContextSerializer extends ValueSerializer<PgContext> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final PgContext value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        gen.writeStringProperty(PgContext.SCHEMA_NAME_FIELD, value.getSchemaName());
        gen.writeNumberProperty(PgContext.BLOAT_PERCENTAGE_THRESHOLD_FIELD, value.getBloatPercentageThreshold());
        gen.writeNumberProperty(PgContext.REMAINING_PERCENTAGE_THRESHOLD_FIELD, value.getRemainingPercentageThreshold());
        gen.writeEndObject();
    }
}
