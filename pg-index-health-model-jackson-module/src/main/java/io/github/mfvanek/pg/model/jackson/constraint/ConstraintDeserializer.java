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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintNameAware;
import io.github.mfvanek.pg.model.constraint.ConstraintType;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;

/**
 * A deserializer for {@link Constraint} objects, enabling JSON deserialization into immutable {@code Constraint} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ConstraintDeserializer extends ModelDeserializer<Constraint> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Constraint deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String tableName = getTableName(ctxt, node);
        final String constraintName = getStringField(ctxt, node, ConstraintNameAware.CONSTRAINT_NAME_FIELD);
        final String constraintType = getStringField(ctxt, node, Constraint.CONSTRAINT_TYPE_FIELD);
        return Constraint.ofType(tableName, constraintName, ConstraintType.valueOf(constraintType));
    }
}
