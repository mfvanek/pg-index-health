/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.column;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.column.ColumnTypeAware;
import io.github.mfvanek.pg.model.column.ColumnWithType;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link ColumnWithType} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnWithTypeSerializer extends JsonSerializer<ColumnWithType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final ColumnWithType value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        serializers.defaultSerializeField(ColumnTypeAware.COLUMN_FIELD, value.toColumn(), gen);
        gen.writeStringField(ColumnTypeAware.COLUMN_TYPE_FIELD, value.getColumnType());
        gen.writeEndObject();
    }
}
