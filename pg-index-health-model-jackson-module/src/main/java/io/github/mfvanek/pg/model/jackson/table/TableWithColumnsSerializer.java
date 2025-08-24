/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.table;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.table.TableSizeAware;
import io.github.mfvanek.pg.model.table.TableWithColumns;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link TableWithColumns} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TableWithColumnsSerializer extends JsonSerializer<TableWithColumns> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final TableWithColumns value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        serializers.defaultSerializeField(TableSizeAware.TABLE_FIELD, value.toTable(), gen);
        serializers.defaultSerializeField(ColumnsAware.COLUMNS_FIELD, value.getColumns(), gen);
        gen.writeEndObject();
    }
}
