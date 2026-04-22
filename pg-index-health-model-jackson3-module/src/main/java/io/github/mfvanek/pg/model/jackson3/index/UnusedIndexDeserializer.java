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

import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;


/**
 * A deserializer for {@link UnusedIndex} objects, enabling JSON deserialization into immutable {@code UnusedIndex} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class UnusedIndexDeserializer extends ModelDeserializer<UnusedIndex> {

    /**
     * {@inheritDoc}
     */
    @Override
    public UnusedIndex deserialize(final JsonParser p, final DeserializationContext ctxt) {
        
        final JsonNode rootNode = ctxt.readTree(p);
        final Index index = getIndex(codec, node, ctxt);
        final long indexScans = getLongField(ctxt, node, UnusedIndex.INDEX_SCANS_FIELD);
        return UnusedIndex.of(index, indexScans);
    }
}
