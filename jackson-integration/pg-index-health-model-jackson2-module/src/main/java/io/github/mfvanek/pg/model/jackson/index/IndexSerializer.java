/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.index;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexNameAware;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link Index} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class IndexSerializer extends JsonSerializer<Index> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Index value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(TableNameAware.TABLE_NAME_FIELD, value.getTableName());
        gen.writeStringField(IndexNameAware.INDEX_NAME_FIELD, value.getIndexName());
        gen.writeNumberField(IndexSizeAware.INDEX_SIZE_IN_BYTES_FIELD, value.getIndexSizeInBytes());
        gen.writeEndObject();
    }
}
