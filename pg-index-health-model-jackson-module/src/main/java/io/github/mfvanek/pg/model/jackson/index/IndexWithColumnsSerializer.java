/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link IndexWithColumns} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class IndexWithColumnsSerializer extends JsonSerializer<IndexWithColumns> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final IndexWithColumns value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        serializers.defaultSerializeField(IndexSizeAware.INDEX_FIELD, value.toIndex(), gen);
        serializers.defaultSerializeField(ColumnsAware.COLUMNS_FIELD, value.getColumns(), gen);
        gen.writeEndObject();
    }
}
