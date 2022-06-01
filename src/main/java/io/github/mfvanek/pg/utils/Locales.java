/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import java.util.Locale;

/**
 * Default locale.
 *
 * @author Ivan Vahrushev
 * @since 0.5.0
 */
public final class Locales {

    public static final Locale DEFAULT = Locale.ENGLISH;

    private Locales() {
        throw new UnsupportedOperationException();
    }
}
