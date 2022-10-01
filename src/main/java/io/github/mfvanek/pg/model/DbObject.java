/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A generalized representation of a database object.
 *
 * @author Ivan Vahrushev
 * @since 0.7.0
 */
@Immutable
public abstract class DbObject {

    /**
     * Gets database object name.
     *
     * @return database object name
     */
    @Nonnull
    public abstract String getName();
}
