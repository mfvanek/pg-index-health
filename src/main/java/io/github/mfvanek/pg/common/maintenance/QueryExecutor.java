/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * An abstraction of sql query executor.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 * @see DbObject
 */
@FunctionalInterface
public interface QueryExecutor {

    @Nonnull
    <T extends DbObject> List<T> executeQuery(@Nonnull PgConnection pgConnection,
                                              @Nonnull PgContext pgContext,
                                              @Nonnull String sqlQuery,
                                              @Nonnull ResultSetExtractor<T> rse);
}
