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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnTypeAware;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;

import java.io.IOException;

/**
 * A deserializer for {@link ColumnWithSerialType} objects, enabling JSON deserialization into immutable {@code ColumnWithSerialType} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnWithSerialTypeDeserializer extends JsonDeserializer<ColumnWithSerialType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnWithSerialType deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Column column = codec.treeToValue(node.get(ColumnTypeAware.COLUMN_FIELD), Column.class);
        final SerialType serialType = SerialType.valueOf(node.get(ColumnWithSerialType.SERIAL_TYPE_FIELD).asText());
        final String sequenceName = node.get(SequenceNameAware.SEQUENCE_NAME_FIELD).asText();
        return ColumnWithSerialType.of(column, serialType, sequenceName);
    }
}
