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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableSizeAware;



/**
 * A deserializer for {@link Table} objects, enabling JSON deserialization into immutable {@code Table} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class TableDeserializer extends ModelDeserializer<Table> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Table deserialize(final JsonParser p, final DeserializationContext ctxt) {
        final JsonNode node = p.getCodec().readTree(p);
        final String tableName = getTableName(ctxt, node);
        final long tableSizeInBytes = getLongField(ctxt, node, TableSizeAware.TABLE_SIZE_IN_BYTES_FIELD);
        return Table.of(tableName, tableSizeInBytes);
    }
}
