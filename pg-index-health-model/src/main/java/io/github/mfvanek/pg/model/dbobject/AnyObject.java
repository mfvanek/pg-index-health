/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.dbobject;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A generalized representation of an object in a database.
 *
 * @author Ivan Vakhrushev
 * @see PgObjectType
 * @since 0.13.2
 */
@Immutable
public final class AnyObject implements DbObject, Comparable<AnyObject> {

    private final String objectName;
    private final PgObjectType objectType;

    private AnyObject(@Nonnull final String objectName, @Nonnull final PgObjectType objectType) {
        this.objectName = Validators.notBlank(objectName, "objectName");
        this.objectType = Objects.requireNonNull(objectType, "objectType cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getName() {
        return objectName;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
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

        if (!(other instanceof AnyObject)) {
            return false;
        }

        final AnyObject that = (AnyObject) other;
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
            "objectName='" + objectName + '\'' +
            ", objectType=" + objectType +
            '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nonnull final AnyObject other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (objectType != other.objectType) {
            return objectType.compareTo(other.objectType);
        }
        return objectName.compareTo(other.objectName);
    }

    /**
     * Constructs an {@code AnyObject} instance.
     *
     * @param objectName name of object in a database; should be non-blank.
     * @param objectType type of object in a database; should be non-null.
     * @return {@code AnyObject} instance
     */
    public static AnyObject ofType(@Nonnull final String objectName,
                                   @Nonnull final PgObjectType objectType) {
        return new AnyObject(objectName, objectType);
    }

    /**
     * Constructs an {@code AnyObject} instance with given context.
     *
     * @param pgContext  the schema context to enrich object name; must be non-null.
     * @param objectName name of object in a database; should be non-blank.
     * @param objectType type of object in a database; should be non-null.
     * @return {@code AnyObject} instance
     * @since 0.14.4
     */
    public static AnyObject ofType(@Nonnull final PgContext pgContext,
                                   @Nonnull final String objectName,
                                   @Nonnull final PgObjectType objectType) {
        return ofType(PgContext.enrichWith(objectName, pgContext), objectType);
    }

    /**
     * Constructs an {@code AnyObject} instance.
     *
     * @param objectName name of object in a database; should be non-blank.
     * @param objectType literal type of object in a database; should be non-null.
     * @return {@code AnyObject} instance
     */
    public static AnyObject ofRaw(@Nonnull final String objectName,
                                  @Nonnull final String objectType) {
        return new AnyObject(objectName, PgObjectType.valueFrom(objectType));
    }
}
