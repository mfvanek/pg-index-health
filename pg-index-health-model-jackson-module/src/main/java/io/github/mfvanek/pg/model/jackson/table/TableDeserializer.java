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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.table.TableSizeAware;

import java.io.IOException;

/**
 * A deserializer for {@link Table} objects, enabling JSON deserialization into immutable {@code Table} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TableDeserializer extends JsonDeserializer<Table> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Table deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String tableName = node.get(TableNameAware.TABLE_NAME_FIELD).asText();
        final long tableSizeInBytes = node.get(TableSizeAware.TABLE_SIZE_IN_BYTES_FIELD).asLong();
        return Table.of(tableName, tableSizeInBytes);
    }
}
