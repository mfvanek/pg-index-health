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

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

/**
 * A deserializer for {@link ColumnWithSerialType} objects, enabling JSON deserialization into immutable {@code ColumnWithSerialType} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ColumnWithSerialTypeDeserializer extends ModelDeserializer<ColumnWithSerialType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnWithSerialType deserialize(final JsonParser p, final DeserializationContext ctxt) {
        final JsonNode rootNode = ctxt.readTree(p);
        final Column column = getColumn(rootNode, ctxt);
        final String serialType = getStringField(ctxt, rootNode, ColumnWithSerialType.SERIAL_TYPE_FIELD);
        final String sequenceName = getStringField(ctxt, rootNode, SequenceNameAware.SEQUENCE_NAME_FIELD);
        return ColumnWithSerialType.of(column, SerialType.valueOf(serialType), sequenceName);
    }
}
