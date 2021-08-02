/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.utils.QueryExecutor;
import io.github.mfvanek.pg.utils.ResultSetExtractor;
import io.github.mfvanek.pg.utils.SqlQueryReader;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * Abstract helper class for implementing statistics collection on a specific host in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 */
public abstract class AbstractMaintenance implements HostAware {

    /**
     * A connection to a specific host in the cluster.
     */
    protected final PgConnection pgConnection;

    protected AbstractMaintenance(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection, "pgConnection");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgHost getHost() {
        return pgConnection.getHost();
    }

    protected <T> List<T> executeQuery(@Nonnull final Diagnostics diagnostics,
                                       @Nonnull final PgContext pgContext,
                                       @Nonnull final ResultSetExtractor<T> rse) {
        final String sqlQuery = SqlQueryReader.getQueryFromFile(diagnostics.getSqlQueryFileName());
        return QueryExecutor.executeQueryWithSchema(pgConnection, pgContext, sqlQuery, rse);
    }

    protected <T> List<T> executeQueryWithBloatThreshold(@Nonnull final Diagnostics diagnostics,
                                                         @Nonnull final PgContext pgContext,
                                                         @Nonnull final ResultSetExtractor<T> rse) {
        final String sqlQuery = SqlQueryReader.getQueryFromFile(diagnostics.getSqlQueryFileName());
        return QueryExecutor.executeQueryWithBloatThreshold(pgConnection, pgContext, sqlQuery, rse);
    }
}
