/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.constraint;

import com.fasterxml.jackson.databind.JavaType;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * A deserializer for {@link DuplicatedForeignKeys} objects, enabling JSON deserialization into immutable {@code DuplicatedForeignKeys} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class DuplicatedForeignKeysDeserializer extends ModelDeserializer<DuplicatedForeignKeys> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DuplicatedForeignKeys deserialize(final JsonParser p, final DeserializationContext ctxt) {
        
        final JsonNode rootNode = ctxt.readTree(p);
        final JavaType listType = ctxt.getTypeFactory().constructCollectionType(List.class, ForeignKey.class);
        final JsonNode foreignKeysNode = getNotNullNode(ctxt, node, DuplicatedForeignKeys.FOREIGN_KEYS_FIELD);
        try (JsonParser foreignKeysParser = foreignKeysNode.traverse(codec)) {
            final List<ForeignKey> foreignKeys = codec.readValue(foreignKeysParser, listType);
            return DuplicatedForeignKeys.of(foreignKeys);
        }
    }
}
