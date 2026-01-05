/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnTypeAware;
import io.github.mfvanek.pg.model.column.ColumnWithType;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;

/**
 * A deserializer for {@link ColumnWithType} objects, enabling JSON deserialization into immutable {@code ColumnWithType} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnWithTypeDeserializer extends ModelDeserializer<ColumnWithType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnWithType deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Column column = getColumn(codec, node, ctxt);
        final String columnType = getStringField(ctxt, node, ColumnTypeAware.COLUMN_TYPE_FIELD);
        return ColumnWithType.of(column, columnType);
    }
}
