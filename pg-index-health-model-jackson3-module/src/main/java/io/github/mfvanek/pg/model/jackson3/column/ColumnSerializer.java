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
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.table.TableNameAware;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * A custom JSON serializer for the {@link Column} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ColumnSerializer extends ValueSerializer<Column> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Column value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        gen.writeStringProperty(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        gen.writeStringProperty(ColumnNameAware.COLUMN_NAME_FIELD, value.getColumnName());
        gen.writeBooleanProperty(ColumnNameAware.NOT_NULL_FIELD, value.isNotNull());
        gen.writeEndObject();
    }
}
