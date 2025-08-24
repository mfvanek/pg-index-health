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
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableSizeAware;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;

import java.io.IOException;

/**
 * A deserializer for {@link TableWithMissingIndex} objects, enabling JSON deserialization into immutable {@code TableWithMissingIndex} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TableWithMissingIndexDeserializer extends JsonDeserializer<TableWithMissingIndex> {

    /**
     * {@inheritDoc}
     */
    @Override
    public TableWithMissingIndex deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Table table = codec.treeToValue(node.get(TableSizeAware.TABLE_FIELD), Table.class);
        final long seqScans = node.get(TableWithMissingIndex.SEQ_SCANS_FIELD).asLong();
        final long indexScans = node.get(TableWithMissingIndex.INDEX_SCANS_FIELD).asLong();
        return TableWithMissingIndex.of(table, seqScans, indexScans);
    }
}
