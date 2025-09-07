/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.constraint;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;
import java.util.List;

/**
 * A deserializer for {@link DuplicatedForeignKeys} objects, enabling JSON deserialization into immutable {@code DuplicatedForeignKeys} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class DuplicatedForeignKeysDeserializer extends ModelDeserializer<DuplicatedForeignKeys> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DuplicatedForeignKeys deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final JavaType listType = ctxt.getTypeFactory().constructCollectionType(List.class, ForeignKey.class);
        final JsonNode foreignKeysNode = getNotNullNode(ctxt, node, DuplicatedForeignKeys.FOREIGN_KEYS_FIELD);
        try (JsonParser foreignKeysParser = foreignKeysNode.traverse(codec)) {
            final List<ForeignKey> foreignKeys = codec.readValue(foreignKeysParser, listType);
            return DuplicatedForeignKeys.of(foreignKeys);
        }
    }
}
