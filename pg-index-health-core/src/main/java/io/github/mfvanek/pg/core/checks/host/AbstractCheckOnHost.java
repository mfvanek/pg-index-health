/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.core.checks.extractors.IndexWithSingleColumnExtractor;
import io.github.mfvanek.pg.core.checks.extractors.TableExtractor;
import io.github.mfvanek.pg.core.utils.SqlQueryReader;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * An abstract class for all database checks performed on a specific host.
 *
 * @param <T> represents an object in a database associated with a table
 * @author Ivan Vakhrushev
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

    protected AbstractCheckOnHost(final Class<T> type,
                                  final PgConnection pgConnection,
                                  final Diagnostic diagnostic) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.pgConnection = Objects.requireNonNull(pgConnection, "pgConnection cannot be null");
        this.diagnostic = Objects.requireNonNull(diagnostic, "diagnostic cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Class<T> getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Diagnostic getDiagnostic() {
        return diagnostic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PgHost getHost() {
        return pgConnection.getHost();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<T> check(final PgContext pgContext, final Predicate<? super T> exclusionsFilter) {
        return doCheck(pgContext).stream()
            .filter(exclusionsFilter)
            .toList();
    }

    /**
     * Executes the check in the specified schema.
     * All child classes must implement this method.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of deviations from the specified rule
     */
    protected abstract List<T> doCheck(PgContext pgContext);

    /**
     * Executes query associated with diagnostic and extracts result.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @param rse       the extractor used to extract results from the {@link ResultSet}; must not be null
     * @return list of deviations from the specified rule
     */
    protected final List<T> executeQuery(final PgContext pgContext,
                                         final ResultSetExtractor<T> rse) {
        final String sqlQuery = SqlQueryReader.getQueryFromFile(diagnostic.getSqlQueryFileName());
        return diagnostic.getQueryExecutor().executeQuery(pgConnection, pgContext, sqlQuery, rse);
    }
}
