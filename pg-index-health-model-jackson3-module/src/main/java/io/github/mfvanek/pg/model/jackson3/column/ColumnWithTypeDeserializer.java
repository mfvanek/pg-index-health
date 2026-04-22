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
import io.github.mfvanek.pg.model.column.ColumnTypeAware;
import io.github.mfvanek.pg.model.column.ColumnWithType;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

/**
 * A deserializer for {@link ColumnWithType} objects, enabling JSON deserialization into immutable {@code ColumnWithType} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ColumnWithTypeDeserializer extends ModelDeserializer<ColumnWithType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnWithType deserialize(final JsonParser p, final DeserializationContext ctxt) {
        final JsonNode rootNode = ctxt.readTree(p);
        final Column column = getColumn(rootNode, ctxt);
        final String columnType = getStringField(ctxt, rootNode, ColumnTypeAware.COLUMN_TYPE_FIELD);
        return ColumnWithType.of(column, columnType);
    }
}
