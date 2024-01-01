/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.settings.validation;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class ParamValidators {

    private ParamValidators() {
        throw new UnsupportedOperationException();
    }

    public static String paramValueNotNull(@Nonnull final String value, @Nonnull final String message) {
        return Objects.requireNonNull(value, message).trim();
    }
}
