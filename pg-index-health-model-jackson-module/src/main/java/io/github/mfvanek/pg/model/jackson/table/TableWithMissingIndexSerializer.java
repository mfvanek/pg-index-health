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
import io.github.mfvanek.pg.model.table.TableSizeAware;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link TableWithMissingIndex} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TableWithMissingIndexSerializer extends JsonSerializer<TableWithMissingIndex> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final TableWithMissingIndex value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        serializers.defaultSerializeField(TableSizeAware.TABLE_FIELD, value.toTable(), gen);
        gen.writeNumberField(TableWithMissingIndex.SEQ_SCANS_FIELD, value.getSeqScans());
        gen.writeNumberField(TableWithMissingIndex.INDEX_SCANS_FIELD, value.getIndexScans());
        gen.writeEndObject();
    }
}
