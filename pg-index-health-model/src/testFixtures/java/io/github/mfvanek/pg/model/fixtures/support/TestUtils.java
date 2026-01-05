/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.fixtures.support;

import de.thetaphi.forbiddenapis.SuppressForbidden;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A utility class providing methods to help in testing.
 * This class includes methods for invoking private constructors,
 * typically used to ensure test coverage for classes with private constructors.
 */
public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Invokes a private no-argument constructor of the specified class. This method makes the
     * constructor accessible, instantiates the class, and throws any exception that occurs during
     * the instantiation process. Typically used for testing purposes, such as ensuring coverage
     * of private constructors in utility classes.
     *
     * @param <T> the type of the class whose private constructor is being invoked
     * @param type the {@code Class} object of the target type with a private no-argument constructor
     * @throws IllegalAccessException if the constructor cannot be accessed
     * @throws InstantiationException if the class cannot be instantiated
     * @throws InvocationTargetException if the constructor throws an exception
     * @throws Throwable if any other exception occurs during the instantiation process
     */
    @SuppressWarnings("checkstyle:IllegalThrows")
    @SuppressForbidden
    public static <T> void invokePrivateConstructor(final Class<T> type)
        throws Throwable {
        final Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);

        try {
            constructor.newInstance();
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
