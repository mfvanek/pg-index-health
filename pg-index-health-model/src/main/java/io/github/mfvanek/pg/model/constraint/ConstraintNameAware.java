/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.table.TableNameAware;

/**
 * Represents an entity that is aware of a database constraint name.
 * Classes implementing this interface should provide the name of a specific database constraint.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.3
 */
public interface ConstraintNameAware extends TableNameAware {

    /**
     * A constant representing the field name for the database constraint.
     */
    String CONSTRAINT_NAME_FIELD = "constraintName";

    /**
     * Retrieves the name of the database constraint associated with this entity.
     *
     * @return the name of the constraint as a non-null {@link String}.
     */
    String getConstraintName();

    /**
     * Retrieves type of constraint.
     *
     * @return type of constraint
     * @see ConstraintType
     * @since 0.15.0
     */
    ConstraintType getConstraintType();
}
