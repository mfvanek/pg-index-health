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

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * A deserializer for {@link ForeignKey} objects, enabling JSON deserialization into immutable {@code ForeignKey} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ForeignKeyDeserializer extends ModelDeserializer<ForeignKey> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ForeignKey deserialize(final JsonParser p, final DeserializationContext ctxt) {
        
        final JsonNode rootNode = ctxt.readTree(p);
        final Constraint constraint = getConstraint(codec, node, ctxt);
        final List<Column> columns = getColumns(codec, node, ctxt);
        return ForeignKey.of(constraint, columns);
    }

    private Constraint getConstraint(final ObjectCodec codec,
                                     final JsonNode rootNode,
                                     final DeserializationContext ctxt) {
        return codec.treeToValue(getNotNullNode(ctxt, rootNode, ForeignKey.CONSTRAINT_FIELD), Constraint.class);
    }
}
