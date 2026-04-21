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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintNameAware;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link Constraint} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ConstraintSerializer extends JsonSerializer<Constraint> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Constraint value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        gen.writeStringField(ConstraintNameAware.CONSTRAINT_NAME_FIELD, value.getConstraintName());
        gen.writeStringField(Constraint.CONSTRAINT_TYPE_FIELD, value.getConstraintType().name());
        gen.writeEndObject();
    }
}
