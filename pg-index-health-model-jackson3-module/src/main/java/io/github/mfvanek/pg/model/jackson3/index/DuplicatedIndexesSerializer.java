/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.index;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link DuplicatedIndexes} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class DuplicatedIndexesSerializer extends ValueSerializer<DuplicatedIndexes> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final DuplicatedIndexes value, final JsonGenerator gen, final SerializerProvider serializers) {
        gen.writeStartObject();
        gen.writeStringProperty(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        gen.writeNumberField(DuplicatedIndexes.TOTAL_SIZE_FIELD, value.getTotalSize());
        serializers.defaultSerializeField(DuplicatedIndexes.INDEXES_FIELD, value.getIndexes(), gen);
        gen.writeEndObject();
    }
}
