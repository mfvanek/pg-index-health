/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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

public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException();
    }

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
