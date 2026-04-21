/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.index;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;

/**
 * A deserializer for {@link UnusedIndex} objects, enabling JSON deserialization into immutable {@code UnusedIndex} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class UnusedIndexDeserializer extends ModelDeserializer<UnusedIndex> {

    /**
     * {@inheritDoc}
     */
    @Override
    public UnusedIndex deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Index index = getIndex(codec, node, ctxt);
        final long indexScans = getLongField(ctxt, node, UnusedIndex.INDEX_SCANS_FIELD);
        return UnusedIndex.of(index, indexScans);
    }
}
