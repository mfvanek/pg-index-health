/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.validation;

import javax.annotation.Nonnull;

public final class AdditionalValidators {

    private AdditionalValidators() {
        throw new UnsupportedOperationException();
    }

    public static int validPercent(final int percentValue, @Nonnull final String argumentName) {
        if (percentValue < 0 || percentValue > 100) {
            throw new IllegalArgumentException(argumentName + " should be in the range from 0 to 100 inclusive");
        }
        return percentValue;
    }
}
