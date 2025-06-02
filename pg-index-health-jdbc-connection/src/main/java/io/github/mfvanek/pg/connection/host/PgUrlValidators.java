/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.host;

import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * Utility class providing validation methods for PostgreSQL connection string (URL).
 */
public final class PgUrlValidators {

    private PgUrlValidators() {
        throw new UnsupportedOperationException();
    }

    /**
     * Validates that a PostgreSQL URL is not blank and has a valid format.
     *
     * @param pgUrl        the PostgreSQL URL to validate; must not be {@code null} or blank.
     * @param argumentName the name of the argument being validated, for use in error messages.
     * @return the validated PostgreSQL URL.
     * @throws IllegalArgumentException if the URL is blank or does not start with the expected header.
     */
    public static String pgUrlNotBlankAndValid(final String pgUrl, final String argumentName) {
        Validators.notBlank(pgUrl, argumentName);
        if (!Objects.requireNonNull(pgUrl).startsWith(PgUrlParser.URL_HEADER)) {
            throw new IllegalArgumentException(argumentName + " has invalid format");
        }
        return pgUrl;
    }

    /**
     * Validates that a PostgreSQL URL is not blank and has a valid format.
     *
     * @param pgUrl the PostgreSQL URL to validate; must not be {@code null} or blank.
     * @return the validated PostgreSQL URL.
     */
    static String pgUrlNotBlankAndValid(final String pgUrl) {
        return pgUrlNotBlankAndValid(pgUrl, "pgUrl");
    }

    /**
     * Validates that a hostname is not blank.
     *
     * @param hostName the hostname to validate; must not be {@code null} or blank.
     * @return the validated hostname.
     * @throws IllegalArgumentException if the hostname is blank.
     */
    static String hostNameNotBlank(final String hostName) {
        Validators.notBlank(hostName, "hostName");
        return hostName;
    }

    /**
     * Validates that a port number is within the acceptable range (1024–65535).
     *
     * @param port the port number to validate.
     * @return the validated port number.
     * @throws IllegalArgumentException if the port number is outside the acceptable range.
     */
    static int portInAcceptableRange(final int port) {
        // https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
        if (port < 1024 || port > 65_535) {
            throw new IllegalArgumentException("the port number must be in the range from 1024 to 65535");
        }
        return port;
    }
}
