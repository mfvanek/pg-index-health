/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.host.PgUrlValidators;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Collection;
import java.util.Objects;

/**
 * Utility class providing validation methods for PostgreSQL connection parameters.
 * This class includes methods for validating PostgreSQL URLs, usernames, passwords,
 * hostnames, ports, and ensuring connections meet specific requirements.
 */
final class PgConnectionValidators {

    private PgConnectionValidators() {
        throw new UnsupportedOperationException();
    }

    /**
     * Validates that a username is not blank.
     *
     * @param userName the username to validate; must not be {@code null} or blank.
     * @return the validated username.
     * @throws IllegalArgumentException if the username is blank.
     */
    static String userNameNotBlank(final String userName) {
        Validators.notBlank(userName, "userName");
        return userName;
    }

    /**
     * Validates that a password is not blank.
     *
     * @param password the password to validate; must not be {@code null} or blank.
     * @return the validated password.
     * @throws IllegalArgumentException if the password is blank.
     */
    static String passwordNotBlank(final String password) {
        Validators.notBlank(password, "password");
        return password;
    }

    /**
     * Validates that a collection of PostgreSQL connection URLs is not empty and that each URL is valid.
     *
     * @param connectionUrls the collection of connection URLs; must not be {@code null} or empty.
     * @throws IllegalArgumentException if the collection is empty or any URL is invalid.
     */
    static void connectionUrlsNotEmptyAndValid(final Collection<String> connectionUrls) {
        Objects.requireNonNull(connectionUrls, "connectionUrls");
        if (connectionUrls.isEmpty()) {
            throw new IllegalArgumentException("connectionUrls have to contain at least one url");
        }
        connectionUrls.forEach(url -> PgUrlValidators.pgUrlNotBlankAndValid(url, "connectionUrl"));
    }
}
