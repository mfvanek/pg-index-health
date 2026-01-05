/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import java.util.List;

/**
 * Represents an entity that is aware of database constraints.
 * Classes implementing this interface should provide access to a list of associated constraints.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.3
 */
public interface ConstraintsAware {

    /**
     * Retrieves the list of database constraints associated with this entity.
     *
     * @return a list of {@link ConstraintNameAware} objects. The list may be empty but will never be null.
     */
    List<ConstraintNameAware> getConstraints();
}
