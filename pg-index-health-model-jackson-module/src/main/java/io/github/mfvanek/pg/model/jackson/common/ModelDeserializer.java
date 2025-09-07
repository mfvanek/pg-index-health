/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnTypeAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.table.TableSizeAware;

import java.io.IOException;
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
 * @since 0.20.3
 */
public abstract class ModelDeserializer<T extends DbObject> extends AbstractDeserializer<T> {

    /**
     * Extracts the {@code tableName} field from the given {@code rootNode},
     * ensuring that it is present, non-null, and of type string.
     *
     * @param ctxt     the deserialization context, used for error reporting
     * @param rootNode the root JSON node containing the object data
     * @return the extracted table name as a {@link String}
     * @throws JsonMappingException if the {@code tableName} field is missing, {@code null}, or not a string
     */
    protected final String getTableName(final DeserializationContext ctxt,
                                        final JsonNode rootNode) throws JsonMappingException {
        return getStringField(ctxt, rootNode, TableNameAware.TABLE_NAME_FIELD);
    }

    /**
     * Extracts a {@link Column} object from the given JSON root node using the provided codec.
     * This method ensures the required field for constructing the {@link Column} is present
     * and non-null within the root node. If the field is missing or null, an input mismatch
     * is reported to the deserialization context.
     *
     * @param codec    the object codec used to transform JSON tree nodes into Java objects
     * @param rootNode the root JSON node containing the object data
     * @param ctxt     the deserialization context used for error reporting
     * @return the {@link Column} object deserialized from the specified JSON node
     * @throws IOException if an I/O error occurs during deserialization
     */
    protected final Column getColumn(final ObjectCodec codec,
                                     final JsonNode rootNode,
                                     final DeserializationContext ctxt) throws IOException {
        return codec.treeToValue(getNotNullNode(ctxt, rootNode, ColumnTypeAware.COLUMN_FIELD), Column.class);
    }

    /**
     * Extracts an {@link Index} object from the provided JSON root node using the given codec.
     * This method ensures that the required field for constructing the {@link Index} is present
     * and non-null within the root node. If the field is missing or explicitly null, an input
     * mismatch is reported to the deserialization context.
     *
     * @param codec    the {@link ObjectCodec} used to convert JSON tree nodes into Java objects
     * @param rootNode the root JSON node containing the data for the {@link Index}
     * @param ctxt     the deserialization context used for error handling and reporting
     * @return the {@link Index} object created from the specified JSON node
     * @throws IOException if an I/O error occurs during deserialization
     */
    protected final Index getIndex(final ObjectCodec codec,
                                   final JsonNode rootNode,
                                   final DeserializationContext ctxt) throws IOException {
        return codec.treeToValue(getNotNullNode(ctxt, rootNode, IndexSizeAware.INDEX_FIELD), Index.class);
    }

    /**
     * Deserializes a {@link Table} object from the provided JSON root node using the given codec.
     * This method ensures the required field for constructing the {@link Table} is present
     * and non-null within the root node. If the field is missing or explicitly null, an input
     * mismatch is reported to the deserialization context.
     *
     * @param codec    the {@link ObjectCodec} used to convert JSON tree nodes into Java objects
     * @param rootNode the root JSON node containing the data for the {@link Table}
     * @param ctxt     the deserialization context used for error handling and reporting
     * @return the {@link Table} object created from the specified JSON node
     * @throws IOException if an I/O error occurs during deserialization
     */
    protected final Table getTable(final ObjectCodec codec,
                                   final JsonNode rootNode,
                                   final DeserializationContext ctxt) throws IOException {
        return codec.treeToValue(getNotNullNode(ctxt, rootNode, TableSizeAware.TABLE_FIELD), Table.class);
    }

    /**
     * Deserializes a list of {@link Column} objects from a specified JSON node using the provided codec.
     * This method ensures the required field for the collection of columns is present
     * and non-null within the root node. If the field is missing or null, an input mismatch
     * is reported to the deserialization context.
     *
     * @param codec    the {@link ObjectCodec} used to transform JSON data into Java objects
     * @param rootNode the root JSON node containing the collection of columns
     * @param ctxt     the deserialization context used for error reporting
     * @return a {@link List} of {@link Column} objects deserialized from the specified JSON node
     * @throws IOException if an I/O error occurs during deserialization
     */
    protected final List<Column> getColumns(final ObjectCodec codec,
                                            final JsonNode rootNode,
                                            final DeserializationContext ctxt) throws IOException {
        final JavaType listType = ctxt.getTypeFactory().constructCollectionType(List.class, Column.class);
        final JsonNode columnsNode = getNotNullNode(ctxt, rootNode, ColumnsAware.COLUMNS_FIELD);
        try (JsonParser columnsParser = columnsNode.traverse(codec)) {
            return codec.readValue(columnsParser, listType);
        }
    }
}
