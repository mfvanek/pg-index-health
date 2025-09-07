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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;
import java.util.List;

/**
 * A deserializer for {@link DuplicatedIndexes} objects, enabling JSON deserialization into immutable {@code DuplicatedIndexes} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class DuplicatedIndexesDeserializer extends ModelDeserializer<DuplicatedIndexes> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DuplicatedIndexes deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final JavaType listType = ctxt.getTypeFactory().constructCollectionType(List.class, Index.class);
        final JsonNode indexesNode = getNotNullNode(ctxt, node, DuplicatedIndexes.INDEXES_FIELD);
        try (JsonParser columnsParser = indexesNode.traverse(codec)) {
            final List<Index> duplicatedIndexes = codec.readValue(columnsParser, listType);
            return DuplicatedIndexes.of(duplicatedIndexes);
        }
    }
}
