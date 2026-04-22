/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.column;

import io.github.mfvanek.pg.model.column.ColumnTypeAware;
import io.github.mfvanek.pg.model.column.ColumnWithType;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;


/**
 * A custom JSON serializer for the {@link ColumnWithType} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ColumnWithTypeSerializer extends ValueSerializer<ColumnWithType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final ColumnWithType value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        ctxt.defaultSerializeProperty(ColumnTypeAware.COLUMN_FIELD, value.toColumn(), gen);
        gen.writeStringProperty(ColumnTypeAware.COLUMN_TYPE_FIELD, value.getColumnType());
        gen.writeEndObject();
    }
}
