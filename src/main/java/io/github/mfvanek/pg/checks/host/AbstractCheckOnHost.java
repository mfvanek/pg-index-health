/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.SqlQueryReader;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * An abstract class for all database checks performed on a specific host.
 *
 * @param <T> represents an object in a database associated with a table
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
abstract class AbstractCheckOnHost<T extends TableNameAware> implements DatabaseCheckOnHost<T> {

    protected static final String TABLE_NAME = "table_name";
    protected static final String INDEX_NAME = "index_name";
    protected static final String TABLE_SIZE = "table_size";
    protected static final String INDEX_SIZE = "index_size";
    protected static final String BLOAT_SIZE = "bloat_size";
    protected static final String BLOAT_PERCENTAGE = "bloat_percentage";

    /**
     * An original java type representing database object.
     */
    private final Class<T> type;
    /**
     * A connection to a specific host in the cluster.
     */
    private final PgConnection pgConnection;
    /**
     * A rule related to the check.
     */
    private final Diagnostic diagnostic;

    protected AbstractCheckOnHost(@Nonnull final Class<T> type,
                                  @Nonnull final PgConnection pgConnection,
                                  @Nonnull final Diagnostic diagnostic) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.pgConnection = Objects.requireNonNull(pgConnection, "pgConnection cannot be null");
        this.diagnostic = Objects.requireNonNull(diagnostic, "diagnostic cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Class<T> getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Diagnostic getDiagnostic() {
        return diagnostic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgHost getHost() {
        return pgConnection.getHost();
    }

    @Nonnull
    protected List<T> executeQuery(@Nonnull final PgContext pgContext,
                                   @Nonnull final ResultSetExtractor<T> rse) {
        final String sqlQuery = SqlQueryReader.getQueryFromFile(diagnostic.getSqlQueryFileName());
        return diagnostic.getQueryExecutor().executeQuery(pgConnection, pgContext, sqlQuery, rse);
    }
}
