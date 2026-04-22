/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.common;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnTypeAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.table.TableSizeAware;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.MismatchedInputException;

import java.util.List;

/**
 * Abstract base deserializer for {@link DbObject} instances that provides
 * common validation helpers for JSON field extraction.
 * <p>
 * Subclasses can extend this deserializer to implement custom parsing logic
 * for specific database-related domain objects while leveraging built-in
 * validation utilities.
 *
 * @param <T> the type of {@link DbObject} being deserialized
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public abstract class ModelDeserializer<T extends DbObject> extends AbstractDeserializer<T> {

    /**
     * Extracts the {@code tableName} field from the given {@code rootNode},
     * ensuring that it is present, non-null, and of type string.
     *
     * @param ctxt     the deserialization context, used for error reporting
     * @param rootNode the root JSON node containing the object data
     * @return the extracted table name as a {@link String}
     * @throws MismatchedInputException if the {@code tableName} field is missing, null, or not a string
     */
    protected final String getTableName(final DeserializationContext ctxt,
                                        final JsonNode rootNode) {
        return getStringField(ctxt, rootNode, TableNameAware.TABLE_NAME_FIELD);
    }

    /**
     * Extracts a {@link Column} object from the provided JSON root node.
     * This method ensures the required field for constructing the {@link Column}
     * is present and non-null within the root node. If the field is missing
     * or explicitly null, an input mismatch is reported to the deserialization context.
     *
     * @param rootNode the root JSON node containing the data for the {@link Column}
     * @param ctxt     the deserialization context used for error handling and reporting
     * @return the {@link Column} object created from the specified JSON node
     */
    protected final Column getColumn(final JsonNode rootNode,
                                     final DeserializationContext ctxt) {
        final JsonNode notNullNode = getNotNullNode(ctxt, rootNode, ColumnTypeAware.COLUMN_FIELD);
        try (JsonParser tokens = ctxt.treeAsTokens(notNullNode)) {
            return tokens.readValueAs(Column.class);
        }
    }

    /**
     * Extracts an {@link Index} object from the provided JSON root node.
     * This method ensures the required field for constructing the {@link Index}
     * is present and non-null within the root node. If the field is missing or
     * explicitly null, an input mismatch is reported to the deserialization context.
     *
     * @param rootNode the root JSON node containing the data for the {@link Index}
     * @param ctxt     the deserialization context used for error handling and reporting
     * @return the {@link Index} object created from the specified JSON node
     */
    protected final Index getIndex(final JsonNode rootNode,
                                   final DeserializationContext ctxt) {
        final JsonNode notNullNode = getNotNullNode(ctxt, rootNode, IndexSizeAware.INDEX_FIELD);
        try (JsonParser tokens = ctxt.treeAsTokens(notNullNode)) {
            return tokens.readValueAs(Index.class);
        }
    }

    /**
     * Extracts a {@link Table} object from the provided JSON root node.
     * This method ensures that the required field for constructing the {@link Table}
     * is present and non-null within the root node. If the field is missing
     * or explicitly null, an input mismatch is reported to the deserialization context.
     *
     * @param rootNode the root JSON node containing the data for the {@link Table}
     * @param ctxt     the deserialization context used for error handling and reporting
     * @return the {@link Table} object created from the specified JSON node
     */
    protected final Table getTable(final JsonNode rootNode,
                                   final DeserializationContext ctxt) {
        final JsonNode notNullNode = getNotNullNode(ctxt, rootNode, TableSizeAware.TABLE_FIELD);
        try (JsonParser tokens = ctxt.treeAsTokens(notNullNode)) {
            return tokens.readValueAs(Table.class);
        }
    }

    /**
     * Extracts a list of {@link Column} objects from the provided JSON root node.
     * This method ensures the required field for constructing the list of {@link Column} objects
     * is present, non-null, and properly formatted within the root node. If the field is missing
     * or explicitly null, an input mismatch is reported to the deserialization context.
     *
     * @param rootNode the root JSON node containing the data for the {@link Column} list
     * @param ctxt     the deserialization context used for error handling and reporting
     * @return a list of {@link Column} objects created from the specified JSON node
     */
    protected final List<Column> getColumns(final JsonNode rootNode,
                                            final DeserializationContext ctxt) {
        final JavaType listType = ctxt.getTypeFactory().constructCollectionType(List.class, Column.class);
        final JsonNode columnsNode = getNotNullNode(ctxt, rootNode, ColumnsAware.COLUMNS_FIELD);
        try (JsonParser columnsParser = columnsNode.traverse(ctxt)) {
            return ctxt.readValue(columnsParser, listType);
        }
    }
}
