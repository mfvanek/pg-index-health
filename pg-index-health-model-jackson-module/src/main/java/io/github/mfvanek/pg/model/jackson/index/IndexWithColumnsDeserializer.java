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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.io.IOException;
import java.util.List;

/**
 * A deserializer for {@link IndexWithColumns} objects, enabling JSON deserialization into immutable {@code IndexWithColumns} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class IndexWithColumnsDeserializer extends JsonDeserializer<IndexWithColumns> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexWithColumns deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final ObjectCodec codec = p.getCodec();
        final JsonNode node = codec.readTree(p);
        final Index index = codec.treeToValue(node.get(IndexSizeAware.INDEX_FIELD), Index.class);
        final JavaType listType = ctxt.getTypeFactory().constructCollectionType(List.class, Column.class);
        try (JsonParser columnsParser = node.get(ColumnsAware.COLUMNS_FIELD).traverse(codec)) {
            final List<Column> columns = codec.readValue(columnsParser, listType);
            return IndexWithColumns.of(index, columns);
        }
    }
}
