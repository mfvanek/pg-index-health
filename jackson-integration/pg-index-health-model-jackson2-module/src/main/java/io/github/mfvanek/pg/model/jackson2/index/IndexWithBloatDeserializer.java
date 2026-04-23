/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson2.index;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.jackson2.common.ModelDeserializer;

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
        final JsonNode rootNode = codec.readTree(p);
        final Index index = getIndex(codec, rootNode, ctxt);
        final long bloatSizeInBytes = getLongField(ctxt, rootNode, BloatAware.BLOAT_SIZE_IN_BYTES_FIELD);
        final double bloatPercentage = getDoubleField(ctxt, rootNode, BloatAware.BLOAT_PERCENTAGE_FIELD);
        return IndexWithBloat.of(index, bloatSizeInBytes, bloatPercentage);
    }
}
