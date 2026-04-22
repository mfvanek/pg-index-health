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

import com.fasterxml.jackson.databind.JavaType;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * A deserializer for {@link DuplicatedIndexes} objects, enabling JSON deserialization into immutable {@code DuplicatedIndexes} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class DuplicatedIndexesDeserializer extends ModelDeserializer<DuplicatedIndexes> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DuplicatedIndexes deserialize(final JsonParser p, final DeserializationContext ctxt) {
        
        final JsonNode rootNode = ctxt.readTree(p);
        final JavaType listType = ctxt.getTypeFactory().constructCollectionType(List.class, Index.class);
        final JsonNode indexesNode = getNotNullNode(ctxt, node, DuplicatedIndexes.INDEXES_FIELD);
        try (JsonParser duplicatedIndexesParser = indexesNode.traverse(codec)) {
            final List<Index> duplicatedIndexes = codec.readValue(duplicatedIndexesParser, listType);
            return DuplicatedIndexes.of(duplicatedIndexes);
        }
    }
}
