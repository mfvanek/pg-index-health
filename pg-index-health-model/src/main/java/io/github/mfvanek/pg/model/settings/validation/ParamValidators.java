/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.settings.validation;

import java.util.Objects;

/**
 * Utility class for validating PostgreSQL parameter values.
 */
public final class ParamValidators {

    private ParamValidators() {
        throw new UnsupportedOperationException();
    }

    /**
     * Validates that the given parameter value is not {@code null}, and returns the trimmed value.
     *
     * @param value   the parameter value to check
     * @param message the exception message if the value is {@code null}
     * @return the trimmed, non-null parameter value
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public static String paramValueNotNull(final String value, final String message) {
        return Objects.requireNonNull(value, message).trim();
    }
}
