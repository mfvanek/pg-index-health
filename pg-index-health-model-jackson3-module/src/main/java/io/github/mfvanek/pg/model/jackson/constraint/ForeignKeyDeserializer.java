/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;
import java.util.List;

/**
 * A deserializer for {@link ForeignKey} objects, enabling JSON deserialization into immutable {@code ForeignKey} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ForeignKeyDeserializer extends ModelDeserializer<ForeignKey> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ForeignKey deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Constraint constraint = getConstraint(codec, node, ctxt);
        final List<Column> columns = getColumns(codec, node, ctxt);
        return ForeignKey.of(constraint, columns);
    }

    private Constraint getConstraint(final ObjectCodec codec,
                                     final JsonNode rootNode,
                                     final DeserializationContext ctxt) throws IOException {
        return codec.treeToValue(getNotNullNode(ctxt, rootNode, ForeignKey.CONSTRAINT_FIELD), Constraint.class);
    }
}
