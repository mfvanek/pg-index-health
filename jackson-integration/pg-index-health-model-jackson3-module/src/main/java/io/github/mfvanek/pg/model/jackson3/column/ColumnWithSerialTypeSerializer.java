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
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * A custom JSON serializer for the {@link ColumnWithSerialType} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ColumnWithSerialTypeSerializer extends ValueSerializer<ColumnWithSerialType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final ColumnWithSerialType value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        ctxt.defaultSerializeProperty(ColumnTypeAware.COLUMN_FIELD, value.toColumn(), gen);
        gen.writeStringProperty(ColumnTypeAware.COLUMN_TYPE_FIELD, value.getColumnType());
        gen.writeStringProperty(ColumnWithSerialType.SERIAL_TYPE_FIELD, value.getSerialType().name());
        gen.writeStringProperty(SequenceNameAware.SEQUENCE_NAME_FIELD, value.getSequenceName());
        gen.writeEndObject();
    }
}
