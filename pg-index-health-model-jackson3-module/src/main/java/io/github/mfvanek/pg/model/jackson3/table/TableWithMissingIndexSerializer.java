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

import io.github.mfvanek.pg.model.table.TableSizeAware;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;


/**
 * A custom JSON serializer for the {@link TableWithMissingIndex} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class TableWithMissingIndexSerializer extends ValueSerializer<TableWithMissingIndex> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final TableWithMissingIndex value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        ctxt.defaultSerializeProperty(TableSizeAware.TABLE_FIELD, value.toTable(), gen);
        gen.writeNumberField(TableWithMissingIndex.SEQ_SCANS_FIELD, value.getSeqScans());
        gen.writeNumberField(TableWithMissingIndex.INDEX_SCANS_FIELD, value.getIndexScans());
        gen.writeEndObject();
    }
}
