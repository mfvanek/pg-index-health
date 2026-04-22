/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.table;

import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.table.TableSizeAware;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * A custom JSON serializer for the {@link TableWithBloat} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class TableWithBloatSerializer extends ValueSerializer<TableWithBloat> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final TableWithBloat value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        ctxt.defaultSerializeProperty(TableSizeAware.TABLE_FIELD, value.toTable(), gen);
        gen.writeNumberProperty(BloatAware.BLOAT_SIZE_IN_BYTES_FIELD, value.getBloatSizeInBytes());
        gen.writeNumberProperty(BloatAware.BLOAT_PERCENTAGE_FIELD, value.getBloatPercentage());
        gen.writeEndObject();
    }
}
