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

import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.table.TableNameAware;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * A custom JSON serializer for the {@link DuplicatedForeignKeys} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class DuplicatedForeignKeysSerializer extends ValueSerializer<DuplicatedForeignKeys> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final DuplicatedForeignKeys value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        gen.writeStringProperty(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        ctxt.defaultSerializeProperty(DuplicatedForeignKeys.FOREIGN_KEYS_FIELD, value.getForeignKeys(), gen);
        gen.writeEndObject();
    }
}
