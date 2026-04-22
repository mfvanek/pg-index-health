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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link Column} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnSerializer extends JsonSerializer<Column> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Column value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        gen.writeStringField(ColumnNameAware.COLUMN_NAME_FIELD, value.getColumnName());
        gen.writeBooleanField(ColumnNameAware.NOT_NULL_FIELD, value.isNotNull());
        gen.writeEndObject();
    }
}
