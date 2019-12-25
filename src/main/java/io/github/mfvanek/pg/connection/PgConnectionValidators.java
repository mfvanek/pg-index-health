/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.connection;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

final class PgConnectionValidators {

    private PgConnectionValidators() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    static String pgUrlNotBlankAndValid(@Nonnull final String pgUrl, @Nonnull final String argumentName) {
        notBlank(pgUrl, argumentName);
        if (!Objects.requireNonNull(pgUrl).startsWith("jdbc:postgresql://")) {
            throw new IllegalArgumentException(argumentName + " has invalid format");
        }
        return pgUrl;
    }

    static void userNameNotBlank(@Nonnull final String userName) {
        notBlank(userName, "userName");
    }

    static void passwordNotBlank(@Nonnull final String password) {
        notBlank(password, "password");
    }

    private static void notBlank(@Nonnull final String argumentValue, @Nonnull final String argumentName) {
        if (StringUtils.isBlank(Objects.requireNonNull(argumentValue, argumentName + " cannot be null"))) {
            throw new IllegalArgumentException(argumentName);
        }
    }
}
