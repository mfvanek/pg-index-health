/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import io.github.mfvanek.pg.model.dbobject.DbObject;

/**
 * Allows getting information about the original generic type.
 *
 * @param <T> represents an object in a database associated with a table
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public interface RawTypeAware<T extends DbObject> {

    /**
     * Retrieves original java type.
     *
     * @return java type representing database object
     */
    Class<T> getType();
}
