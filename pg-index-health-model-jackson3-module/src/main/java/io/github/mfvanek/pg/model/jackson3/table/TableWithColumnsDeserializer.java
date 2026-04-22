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

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithColumns;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * A deserializer for {@link TableWithColumns} objects, enabling JSON deserialization into immutable {@code TableWithColumns} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class TableWithColumnsDeserializer extends ModelDeserializer<TableWithColumns> {

    /**
     * {@inheritDoc}
     */
    @Override
    public TableWithColumns deserialize(final JsonParser p, final DeserializationContext ctxt) {
        
        final JsonNode rootNode = ctxt.readTree(p);
        final Table table = getTable(codec, node, ctxt);
        final List<Column> columns = getColumns(codec, node, ctxt);
        return TableWithColumns.of(table, columns);
    }
}
