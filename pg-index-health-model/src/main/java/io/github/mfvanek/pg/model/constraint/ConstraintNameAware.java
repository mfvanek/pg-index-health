/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import javax.annotation.Nonnull;

/**
 * Represents an entity that is aware of a database constraint name.
 * Classes implementing this interface should provide the name of a specific database constraint.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.3
 */
public interface ConstraintNameAware {

    /**
     * Retrieves the name of the database constraint associated with this entity.
     *
     * @return the name of the constraint as a non-null {@link String}.
     */
    @Nonnull
    String getConstraintName();
}
