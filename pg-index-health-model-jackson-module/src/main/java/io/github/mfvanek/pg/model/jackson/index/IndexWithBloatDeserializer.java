/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;

/**
 * A deserializer for {@link IndexWithBloat} objects, enabling JSON deserialization into immutable {@code IndexWithBloat} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class IndexWithBloatDeserializer extends ModelDeserializer<IndexWithBloat> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexWithBloat deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Index index = codec.treeToValue(node.get(IndexSizeAware.INDEX_FIELD), Index.class);
        final long bloatSizeInBytes = getLongField(ctxt, node, BloatAware.BLOAT_SIZE_IN_BYTES_FIELD);
        final double bloatPercentage = node.get(BloatAware.BLOAT_PERCENTAGE_FIELD).asDouble();
        return IndexWithBloat.of(index, bloatSizeInBytes, bloatPercentage);
    }
}
