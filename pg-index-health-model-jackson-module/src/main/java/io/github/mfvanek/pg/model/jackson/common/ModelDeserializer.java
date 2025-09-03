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

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.Locale;

/**
 * Abstract base deserializer for {@link DbObject} instances that provides
 * common validation helpers for JSON field extraction.
 * <p>
 * Subclasses can extend this deserializer to implement custom parsing logic
 * for specific database-related domain objects while leveraging built-in
 * validation utilities.
 *
 * @param <T> the type of {@link DbObject} being deserialized
 */
public abstract class ModelDeserializer<T extends DbObject> extends JsonDeserializer<T> {

    /**
     * Retrieves the child node with the given {@code fieldName} from the provided
     * {@code rootNode}, ensuring that it exists and is not {@code null}.
     * <p>
     * If the field is missing or explicitly {@code null}, this method reports an
     * input mismatch to the {@link DeserializationContext}, resulting in a
     * {@link JsonMappingException}.
     *
     * @param ctxt      the deserialization context, used for error reporting
     * @param rootNode  the root JSON node containing the object data
     * @param fieldName the name of the required field to extract
     * @return the non-null {@link JsonNode} associated with {@code fieldName}
     * @throws JsonMappingException if the field is missing or explicitly {@code null}
     */
    protected final JsonNode getNotNullNode(final DeserializationContext ctxt,
                                            final JsonNode rootNode,
                                            final String fieldName) throws JsonMappingException {
        final JsonNode targetNode = rootNode.get(fieldName);
        if (targetNode == null || targetNode.isNull()) {
            final String msg = String.format(Locale.ROOT, "Missing required field: %s", fieldName);
            throw MismatchedInputException.from(ctxt.getParser(), this.handledType(), msg);
        }
        return targetNode;
    }

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
     * Retrieves the value of a specified field from a JSON node as a {@code String}.
     * <p>
     * This method ensures the field exists, is not {@code null}, and is of textual type. If the field
     * is missing, {@code null}, or not a string, an input mismatch is reported to the
     * {@code DeserializationContext}, resulting in a {@code JsonMappingException}.
     *
     * @param ctxt      the deserialization context used for error reporting
     * @param rootNode  the root JSON node containing the field
     * @param fieldName the name of the field to retrieve from {@code rootNode}
     * @return the value of the specified field as a {@code String}
     * @throws JsonMappingException if the field is missing, {@code null}, or not a string
     */
    protected final String getStringField(final DeserializationContext ctxt,
                                          final JsonNode rootNode,
                                          final String fieldName) throws JsonMappingException {
        final JsonNode stringNode = getNotNullNode(ctxt, rootNode, fieldName);
        if (!stringNode.isTextual()) {
            final String msg = String.format(Locale.ROOT, "Field '%s' must be a string", fieldName);
            throw MismatchedInputException.from(ctxt.getParser(), this.handledType(), msg);
        }
        return stringNode.asText();
    }

    /**
     * Retrieves the value of a specified field from a JSON node as a boolean.
     * <p>
     * This method ensures the field exists, is not {@code null}, and is of boolean type.
     * If the field is missing, {@code null}, or not a boolean, an input mismatch is
     * reported to the {@link DeserializationContext}, resulting in a {@link JsonMappingException}.
     *
     * @param ctxt      the deserialization context used for error reporting
     * @param rootNode  the root JSON node containing the field
     * @param fieldName the name of the field to retrieve from {@code rootNode}
     * @return the value of the specified field as a {@code boolean}
     * @throws JsonMappingException if the field is missing, {@code null}, or not a boolean
     */
    protected final boolean getBooleanField(final DeserializationContext ctxt,
                                            final JsonNode rootNode,
                                            final String fieldName) throws JsonMappingException {
        final JsonNode booleanNode = getNotNullNode(ctxt, rootNode, fieldName);
        if (!booleanNode.isBoolean()) {
            final String msg = String.format(Locale.ROOT, "Field '%s' must be a boolean", fieldName);
            throw MismatchedInputException.from(ctxt.getParser(), this.handledType(), msg);
        }
        return booleanNode.asBoolean();
    }
}
