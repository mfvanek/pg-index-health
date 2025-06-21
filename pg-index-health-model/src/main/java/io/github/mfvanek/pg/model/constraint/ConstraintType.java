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

import java.util.Objects;

/**
 * A mapping to PostgreSQL constraint types.
 *
 * @author Blohny
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-constraint.html">pg_constraint</a>
 * @since 0.11.0
 */
public enum ConstraintType {

    /**
     * Check constraint.
     */
    CHECK("c"),
    /**
     * Foreign key constraint.
     */
    FOREIGN_KEY("f");

    private final String pgConType;

    ConstraintType(final String pgConType) {
        this.pgConType = Objects.requireNonNull(pgConType, "pgConType");
    }

    /**
     * Retrieves internal PostgreSQL constraint type.
     *
     * @return pgConType
     */
    public String getPgConType() {
        return pgConType;
    }

    /**
     * Retrieves {@code ConstraintType} from internal PostgreSQL constraint type.
     *
     * @param pgConType internal PostgreSQL constraint type; should be non-null.
     * @return {@code ConstraintType}
     */
    public static ConstraintType valueFrom(final String pgConType) {
        Objects.requireNonNull(pgConType, "pgConType cannot be null");
        for (final ConstraintType ct : values()) {
            if (ct.getPgConType().equals(pgConType)) {
                return ct;
            }
        }
        throw new IllegalArgumentException("Unknown pgConType: " + pgConType);
    }
}
