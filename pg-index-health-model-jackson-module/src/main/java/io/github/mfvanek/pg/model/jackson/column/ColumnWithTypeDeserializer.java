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
import io.github.mfvanek.pg.model.column.ColumnWithType;

import java.io.IOException;

/**
 * A deserializer for {@link ColumnWithType} objects, enabling JSON deserialization into immutable {@code ColumnWithType} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnWithTypeDeserializer extends JsonDeserializer<ColumnWithType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnWithType deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Column column = codec.treeToValue(node.get(ColumnTypeAware.COLUMN_FIELD), Column.class);
        final String columnType = node.get(ColumnTypeAware.COLUMN_TYPE_FIELD).asText();
        return ColumnWithType.of(column, columnType);
    }
}
