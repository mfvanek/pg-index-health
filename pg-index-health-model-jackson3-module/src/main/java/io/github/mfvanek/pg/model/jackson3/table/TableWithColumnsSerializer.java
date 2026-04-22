/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.table;

import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.table.TableSizeAware;
import io.github.mfvanek.pg.model.table.TableWithColumns;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * A custom JSON serializer for the {@link TableWithColumns} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class TableWithColumnsSerializer extends ValueSerializer<TableWithColumns> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final TableWithColumns value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        ctxt.defaultSerializeProperty(TableSizeAware.TABLE_FIELD, value.toTable(), gen);
        ctxt.defaultSerializeProperty(ColumnsAware.COLUMNS_FIELD, value.getColumns(), gen);
        gen.writeEndObject();
    }
}
