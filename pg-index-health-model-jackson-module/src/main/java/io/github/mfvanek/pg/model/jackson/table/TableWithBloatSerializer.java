/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.table;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.table.TableSizeAware;
import io.github.mfvanek.pg.model.table.TableWithBloat;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link TableWithBloat} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TableWithBloatSerializer extends JsonSerializer<TableWithBloat> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final TableWithBloat value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        serializers.defaultSerializeField(TableSizeAware.TABLE_FIELD, value.toTable(), gen);
        gen.writeNumberField(BloatAware.BLOAT_SIZE_IN_BYTES_FIELD, value.getBloatSizeInBytes());
        gen.writeNumberField(BloatAware.BLOAT_PERCENTAGE_FIELD, value.getBloatPercentage());
        gen.writeEndObject();
    }
}
