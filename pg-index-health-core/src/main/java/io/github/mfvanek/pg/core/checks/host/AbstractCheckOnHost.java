/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.extractors.IndexWithSingleColumnExtractor;
import io.github.mfvanek.pg.core.checks.extractors.TableExtractor;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.utils.SqlQueryReader;

import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * An abstract class for all database checks performed on a specific host.
 *
 * @param <T> represents an object in a database associated with a table
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
abstract class AbstractCheckOnHost<T extends DbObject> implements DatabaseCheckOnHost<T> {

    protected static final String TABLE_NAME = TableExtractor.TABLE_NAME;
    protected static final String INDEX_NAME = IndexWithSingleColumnExtractor.INDEX_NAME;
    protected static final String TABLE_SIZE = TableExtractor.TABLE_SIZE;
    protected static final String INDEX_SIZE = IndexWithSingleColumnExtractor.INDEX_SIZE;
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

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final List<T> check(@Nonnull final PgContext pgContext, @Nonnull final Predicate<? super T> exclusionsFilter) {
        return doCheck(pgContext).stream()
            .filter(exclusionsFilter)
            .collect(Collectors.toList());
    }

    /**
     * Executes the check in the specified schema.
     * All child classes must implement this method.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of deviations from the specified rule
     */
    @Nonnull
    protected abstract List<T> doCheck(@Nonnull PgContext pgContext);

    /**
     * Executes query associated with diagnostic and extracts result.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @param rse       the extractor used to extract results from the {@link ResultSet}; must not be null
     * @return list of deviations from the specified rule
     */
    @Nonnull
    protected final List<T> executeQuery(@Nonnull final PgContext pgContext,
                                         @Nonnull final ResultSetExtractor<T> rse) {
        final String sqlQuery = SqlQueryReader.getQueryFromFile(diagnostic.getSqlQueryFileName());
        return diagnostic.getQueryExecutor().executeQuery(pgConnection, pgContext, sqlQuery, rse);
    }
}
