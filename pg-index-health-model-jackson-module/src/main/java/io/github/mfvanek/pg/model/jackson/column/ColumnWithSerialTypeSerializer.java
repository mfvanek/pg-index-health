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
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link ColumnWithSerialType} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnWithSerialTypeSerializer extends JsonSerializer<ColumnWithSerialType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final ColumnWithSerialType value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        serializers.defaultSerializeField(ColumnWithSerialType.COLUMN_FIELD, value.toColumn(), gen);
        gen.writeStringField(ColumnWithSerialType.SERIAL_TYPE_FIELD, value.getSerialType().name());
        gen.writeStringField(ColumnWithSerialType.SEQUENCE_NAME_FIELD, value.getSequenceName());
        gen.writeEndObject();
    }
}
