/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * A mapping to PostgreSQL constraint types.
 *
 * @author Blohny
 * @since 0.10.4
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-constraint.html">pg_constraint</a>
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

    ConstraintType(@Nonnull final String pgConType) {
        this.pgConType = Objects.requireNonNull(pgConType, "pgConType");
    }

    /**
     * Gets internal PostgreSQL constraint type.
     *
     * @return pgConType
     */
    @Nonnull
    public String getPgConType() {
        return pgConType;
    }

    /**
     * Gets {@code ConstraintType} from internal PostgreSQL constraint type.
     *
     * @param pgConType internal PostgreSQL constraint type; should be non-null.
     * @return {@code ConstraintType}
     */
    @Nonnull
    public static ConstraintType valueFrom(@Nonnull final String pgConType) {
        Objects.requireNonNull(pgConType, "pgConType cannot be null");
        for (final ConstraintType ct : values()) {
            if (ct.getPgConType().equals(pgConType)) {
                return ct;
            }
        }
        throw new IllegalArgumentException("Unknown pgConType: " + pgConType);
    }
}
