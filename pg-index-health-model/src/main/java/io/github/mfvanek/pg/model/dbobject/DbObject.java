/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.dbobject;

/**
 * A generalized representation of a database object.
 * Mostly used as a marker interface.
 *
 * @author Ivan Vakhrushev
 * @since 0.7.0
 */
public interface DbObject {

    /**
     * Retrieves database object name.
     *
     * @return database object name
     */
    String getName();

    /**
     * Retrieves database object type.
     *
     * @return database object type
     * @since 0.13.2
     */
    PgObjectType getObjectType();
}
