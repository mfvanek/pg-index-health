/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.dbobject;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable generalized representation of an object in a database.
 *
 * @author Ivan Vakhrushev
 * @see PgObjectType
 * @since 0.13.2
 */
public final class AnyObject implements DbObject, Comparable<AnyObject> {

    public static final String OBJECT_NAME_FIELD = "objectName";
    public static final String OBJECT_TYPE_FIELD = "objectType";

    private final String objectName;
    private final PgObjectType objectType;

    private AnyObject(final String objectName, final PgObjectType objectType) {
        this.objectName = Validators.notBlank(objectName, OBJECT_NAME_FIELD);
        this.objectType = Objects.requireNonNull(objectType, OBJECT_TYPE_FIELD + " cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return objectName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return objectType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final AnyObject that)) {
            return false;
        }

        return Objects.equals(objectName, that.objectName) &&
            Objects.equals(objectType, that.objectType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(objectName, objectType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return AnyObject.class.getSimpleName() + '{' +
            OBJECT_NAME_FIELD + "='" + objectName + '\'' +
            ", " + OBJECT_TYPE_FIELD + '=' + objectType +
            '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final AnyObject other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (objectType != other.objectType) {
            return objectType.compareTo(other.objectType);
        }
        return objectName.compareTo(other.objectName);
    }

    /**
     * Constructs an {@code AnyObject} instance.
     *
     * @param objectName name of an object in a database; should be non-blank.
     * @param objectType type of object in a database; should be non-null.
     * @return {@code AnyObject} instance
     */
    public static AnyObject ofType(final String objectName,
                                   final PgObjectType objectType) {
        return new AnyObject(objectName, objectType);
    }

    /**
     * Constructs an {@code AnyObject} instance with given context.
     *
     * @param pgContext  the schema context to enrich object name; must be non-null.
     * @param objectName name of an object in a database; should be non-blank.
     * @param objectType type of object in a database; should be non-null.
     * @return {@code AnyObject} instance
     * @since 0.14.4
     */
    public static AnyObject ofType(final PgContext pgContext,
                                   final String objectName,
                                   final PgObjectType objectType) {
        return ofType(PgContext.enrichWith(objectName, pgContext), objectType);
    }

    /**
     * Constructs an {@code AnyObject} instance.
     *
     * @param objectName name of an object in a database; should be non-blank.
     * @param objectType literal type of object in a database; should be non-null.
     * @return {@code AnyObject} instance
     */
    public static AnyObject ofRaw(final String objectName,
                                  final String objectType) {
        return new AnyObject(objectName, PgObjectType.valueFrom(objectType));
    }
}
