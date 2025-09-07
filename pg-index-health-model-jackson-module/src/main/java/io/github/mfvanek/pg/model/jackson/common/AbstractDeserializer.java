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

import java.util.Locale;

/**
 * Base class for custom Jackson deserializers. Provides utility methods for
 * extracting and validating fields from JSON nodes.
 *
 * @param <T> the type of object to be deserialized
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public abstract class AbstractDeserializer<T> extends JsonDeserializer<T> {

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

    /**
     * Retrieves the value of a specified field from a JSON node as a {@code long}.
     * <p>
     * This method ensures the field exists, is not {@code null}, and is of type long.
     * If the field is missing, {@code null}, or not a long, an input mismatch is reported
     * to the {@link DeserializationContext}, resulting in a {@link JsonMappingException}.
     *
     * @param ctxt      the deserialization context used for error reporting
     * @param rootNode  the root JSON node containing the field
     * @param fieldName the name of the field to retrieve from {@code rootNode}
     * @return the value of the specified field as a {@code long}
     * @throws JsonMappingException if the field is missing, {@code null}, or not a long
     */
    protected final long getLongField(final DeserializationContext ctxt,
                                      final JsonNode rootNode,
                                      final String fieldName) throws JsonMappingException {
        final JsonNode longNode = getNotNullNode(ctxt, rootNode, fieldName);
        if (!longNode.canConvertToLong()) {
            final String msg = String.format(Locale.ROOT, "Field '%s' must be a long", fieldName);
            throw MismatchedInputException.from(ctxt.getParser(), this.handledType(), msg);
        }
        return longNode.asLong();
    }

    /**
     * Retrieves the value of a specified field from a JSON node as a {@code double}.
     * <p>
     * This method ensures the field exists, is not {@code null}, and is of type double.
     * If the field is missing, {@code null}, or not a double, an input mismatch is
     * reported to the {@code DeserializationContext}, resulting in a {@code JsonMappingException}.
     *
     * @param ctxt      the deserialization context used for error reporting
     * @param rootNode  the root JSON node containing the field
     * @param fieldName the name of the field to retrieve from {@code rootNode}
     * @return the value of the specified field as a {@code double}
     * @throws JsonMappingException if the field is missing, {@code null}, or not a double
     */
    protected final double getDoubleField(final DeserializationContext ctxt,
                                          final JsonNode rootNode,
                                          final String fieldName) throws JsonMappingException {
        final JsonNode doubleNode = getNotNullNode(ctxt, rootNode, fieldName);
        if (!doubleNode.isNumber()) {
            final String msg = String.format(Locale.ROOT, "Field '%s' must be a double", fieldName);
            throw MismatchedInputException.from(ctxt.getParser(), this.handledType(), msg);
        }
        return doubleNode.asDouble();
    }
}
