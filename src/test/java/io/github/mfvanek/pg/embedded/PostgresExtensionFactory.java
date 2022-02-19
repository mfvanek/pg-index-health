/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.embedded;

/**
 * Factory to provide JUnit test extension for PostgreSQL instance.
 *
 * @author Nikolay Kondratyev
 */
public final class PostgresExtensionFactory {

    private PostgresExtensionFactory() {}

    public static PostgresDbExtension database() {
        return new PostgresDbExtension();
    }
}
