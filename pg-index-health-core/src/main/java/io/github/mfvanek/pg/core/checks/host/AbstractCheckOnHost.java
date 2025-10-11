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
import io.github.mfvanek.pg.core.checks.common.CheckInfo;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.ExecutionTopology;
import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.core.checks.common.StandardCheckInfo;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * An abstract implementation of the {@link DatabaseCheckOnHost} interface, providing
 * a framework for checks to be performed on a specific PostgreSQL host.
 * <p>
 * Subclasses are required to define the specific check logic in the {@link AbstractCheckOnHost#doCheck(PgContext)} method.
 *
 * @param <T> the type of the database object on which the check is performed
 * @author Ivan Vakhrushev
 * @see StandardCheckInfo
 * @since 0.6.0
 */
public abstract class AbstractCheckOnHost<T extends DbObject> implements DatabaseCheckOnHost<T> {

    /**
     * An original java type representing a database object.
     */
    protected final Class<T> type;
    /**
     * A connection to a specific host in the cluster.
     */
    protected final PgConnection pgConnection;
    /**
     * Represents the configuration and metadata for a specific diagnostic or
     * check to be performed on a PostgreSQL host.
     */
    protected final CheckInfo checkInfo;

    /**
     * Constructs an instance of AbstractCheckOnHost with the specified parameters.
     *
     * @param type         the type of the entity being checked; must not be null
     * @param pgConnection the PostgreSQL connection associated with this check; must not be null
     * @param checkInfo    the diagnostic defining the check configuration; must not be null
     * @see StandardCheckInfo
     */
    protected AbstractCheckOnHost(final Class<T> type,
                                  final PgConnection pgConnection,
                                  final CheckInfo checkInfo) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.pgConnection = Objects.requireNonNull(pgConnection, "pgConnection cannot be null");
        this.checkInfo = Objects.requireNonNull(checkInfo, "checkInfo cannot be null");
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
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return checkInfo.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRuntime() {
        return checkInfo.isRuntime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionTopology getExecutionTopology() {
        return checkInfo.getExecutionTopology();
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
     * Executes a query associated with a diagnostic and extracts the result.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @param rse       the extractor used to extract results from the {@link ResultSet}; must not be null
     * @return list of deviations from the specified rule
     */
    protected final List<T> executeQuery(final PgContext pgContext,
                                         final ResultSetExtractor<T> rse) {
        final String sqlQuery = checkInfo.getSqlQuery();
        return checkInfo.getQueryExecutor().executeQuery(pgConnection, pgContext, sqlQuery, rse);
    }
}
