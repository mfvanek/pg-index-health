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

import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.table.TableSizeAware;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;


/**
 * A custom JSON serializer for the {@link Table} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class TableSerializer extends ValueSerializer<Table> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Table value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        gen.writeStringProperty(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        gen.writeNumberField(TableSizeAware.TABLE_SIZE_IN_BYTES_FIELD, value.getTableSizeInBytes());
        gen.writeEndObject();
    }
}
