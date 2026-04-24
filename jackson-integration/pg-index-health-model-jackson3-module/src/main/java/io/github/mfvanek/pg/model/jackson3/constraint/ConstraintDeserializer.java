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

import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintNameAware;
import io.github.mfvanek.pg.model.constraint.ConstraintType;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

/**
 * A deserializer for {@link Constraint} objects, enabling JSON deserialization into immutable {@code Constraint} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ConstraintDeserializer extends ModelDeserializer<Constraint> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Constraint deserialize(final JsonParser p, final DeserializationContext ctxt) {
        final JsonNode rootNode = ctxt.readTree(p);
        final String tableName = getTableName(ctxt, rootNode);
        final String constraintName = getStringField(ctxt, rootNode, ConstraintNameAware.CONSTRAINT_NAME_FIELD);
        final String constraintType = getStringField(ctxt, rootNode, Constraint.CONSTRAINT_TYPE_FIELD);
        return Constraint.ofType(tableName, constraintName, ConstraintType.valueOf(constraintType));
    }
}
