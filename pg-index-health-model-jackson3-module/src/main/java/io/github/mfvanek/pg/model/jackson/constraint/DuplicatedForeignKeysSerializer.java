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
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link DuplicatedForeignKeys} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class DuplicatedForeignKeysSerializer extends JsonSerializer<DuplicatedForeignKeys> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final DuplicatedForeignKeys value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        serializers.defaultSerializeField(DuplicatedForeignKeys.FOREIGN_KEYS_FIELD, value.getForeignKeys(), gen);
        gen.writeEndObject();
    }
}
