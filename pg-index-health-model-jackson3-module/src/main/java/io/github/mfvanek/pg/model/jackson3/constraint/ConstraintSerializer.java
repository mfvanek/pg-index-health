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
import io.github.mfvanek.pg.model.table.TableNameAware;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;


/**
 * A custom JSON serializer for the {@link Constraint} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ConstraintSerializer extends ValueSerializer<Constraint> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Constraint value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        gen.writeStringProperty(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        gen.writeStringProperty(ConstraintNameAware.CONSTRAINT_NAME_FIELD, value.getConstraintName());
        gen.writeStringProperty(Constraint.CONSTRAINT_TYPE_FIELD, value.getConstraintType().name());
        gen.writeEndObject();
    }
}
