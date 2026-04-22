/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.index;

import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;


/**
 * A custom JSON serializer for the {@link UnusedIndex} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class UnusedIndexSerializer extends ValueSerializer<UnusedIndex> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final UnusedIndex value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        ctxt.defaultSerializeProperty(IndexSizeAware.INDEX_FIELD, value.toIndex(), gen);
        gen.writeNumberField(UnusedIndex.INDEX_SCANS_FIELD, value.getIndexScans());
        gen.writeEndObject();
    }
}
