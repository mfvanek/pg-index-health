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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;

import java.io.IOException;
import java.util.List;

/**
 * A deserializer for {@link IndexWithColumns} objects, enabling JSON deserialization into immutable {@code IndexWithColumns} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class IndexWithColumnsDeserializer extends ModelDeserializer<IndexWithColumns> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexWithColumns deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Index index = getIndex(codec, node, ctxt);
        final List<Column> columns = getColumns(codec, node, ctxt);
        return IndexWithColumns.of(index, columns);
    }
}
