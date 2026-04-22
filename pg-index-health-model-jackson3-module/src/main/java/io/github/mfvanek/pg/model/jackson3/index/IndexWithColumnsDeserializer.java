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

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * A deserializer for {@link IndexWithColumns} objects, enabling JSON deserialization into immutable {@code IndexWithColumns} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class IndexWithColumnsDeserializer extends ModelDeserializer<IndexWithColumns> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexWithColumns deserialize(final JsonParser p, final DeserializationContext ctxt) {
        final JsonNode rootNode = ctxt.readTree(p);
        final Index index = getIndex(rootNode, ctxt);
        final List<Column> columns = getColumns(rootNode, ctxt);
        return IndexWithColumns.of(index, columns);
    }
}
