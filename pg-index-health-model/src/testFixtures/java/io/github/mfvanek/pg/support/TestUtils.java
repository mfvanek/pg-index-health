/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nonnull;

public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("checkstyle:IllegalThrows")
    public static <T> void invokePrivateConstructor(@Nonnull final Class<T> type)
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
