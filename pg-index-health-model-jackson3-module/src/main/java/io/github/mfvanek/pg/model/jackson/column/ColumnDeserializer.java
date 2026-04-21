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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;

/**
 * A deserializer for {@link Column} objects, enabling JSON deserialization into immutable {@code Column} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnDeserializer extends ModelDeserializer<Column> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Column deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String tableName = getTableName(ctxt, node);
        final String columnName = getStringField(ctxt, node, ColumnNameAware.COLUMN_NAME_FIELD);
        final boolean notNull = getBooleanField(ctxt, node, ColumnNameAware.NOT_NULL_FIELD);
        if (notNull) {
            return Column.ofNotNull(tableName, columnName);
        }
        return Column.ofNullable(tableName, columnName);
    }
}
