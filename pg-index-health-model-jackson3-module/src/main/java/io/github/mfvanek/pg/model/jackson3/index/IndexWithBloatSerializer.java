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
import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.index.IndexWithBloat;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link IndexWithBloat} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class IndexWithBloatSerializer extends ValueSerializer<IndexWithBloat> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final IndexWithBloat value, final JsonGenerator gen, final SerializerProvider serializers) {
        gen.writeStartObject();
        serializers.defaultSerializeField(IndexSizeAware.INDEX_FIELD, value.toIndex(), gen);
        gen.writeNumberField(BloatAware.BLOAT_SIZE_IN_BYTES_FIELD, value.getBloatSizeInBytes());
        gen.writeNumberField(BloatAware.BLOAT_PERCENTAGE_FIELD, value.getBloatPercentage());
        gen.writeEndObject();
    }
}
