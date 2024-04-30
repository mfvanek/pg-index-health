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
 * @since 0.10.3
 */
public enum ConstraintType {

    CHECK("c"),
    FOREIGN_KEY("f");

    private final String pgConType;

    ConstraintType(@Nonnull final String pgConType) {
        this.pgConType = Objects.requireNonNull(pgConType, "pgConType");
    }

    @Nonnull
    public String getPgConType() {
        return pgConType;
    }

    @Nonnull
    public static ConstraintType fromConstraintType(final String pgConType) {
        for (final ConstraintType ct : values()) {
            if (ct.getPgConType().equals(pgConType)) {
                return ct;
            }
        }
        throw new IllegalArgumentException("Unknown pgConType: " + pgConType);
    }
}
