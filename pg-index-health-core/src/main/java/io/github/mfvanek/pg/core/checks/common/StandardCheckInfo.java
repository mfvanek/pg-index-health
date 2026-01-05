/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import io.github.mfvanek.pg.core.utils.QueryExecutors;
import io.github.mfvanek.pg.core.utils.SqlQueryReader;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Locale;
import java.util.Objects;

/**
 * Standard implementation of the {@link CheckInfo} interface.
 * This class encapsulates details about a specific check (or diagnostic),
 * including its name, execution topology, associated SQL query, query execution logic,
 * and whether it is a runtime check.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public class StandardCheckInfo implements CheckInfo {

    private final String checkName;
    private final ExecutionTopology executionTopology;
    private final String sqlQuery;
    private final QueryExecutor queryExecutor;
    private final boolean runtimeCheck;

    /**
     * Constructs a new instance of StandardCheckInfo with the specified parameters.
     *
     * @param checkName         the name of the check; must not be blank and will be converted to the upper case
     * @param executionTopology the execution topology specifying where the diagnostic should be executed; must not be null
     * @param sqlQuery          the SQL query associated with the check; must not be blank
     * @param queryExecutor     the query executor for executing the SQL query; must not be null
     * @param runtimeCheck      whether this is a runtime check; required to be true if the execution topology is ACROSS_CLUSTER
     */
    public StandardCheckInfo(final String checkName,
                             final ExecutionTopology executionTopology,
                             final String sqlQuery,
                             final QueryExecutor queryExecutor,
                             final boolean runtimeCheck) {
        this.checkName = Validators.notBlank(checkName, "checkName").toUpperCase(Locale.ROOT);
        this.executionTopology = Objects.requireNonNull(executionTopology, "executionTopology cannot be null");
        this.sqlQuery = Validators.notBlank(sqlQuery, "sqlQuery");
        this.queryExecutor = Objects.requireNonNull(queryExecutor, "queryExecutor cannot be null");
        this.runtimeCheck = runtimeCheck;
        if (executionTopology == ExecutionTopology.ACROSS_CLUSTER && !runtimeCheck) {
            throw new IllegalArgumentException("Runtime check is required for across cluster execution");
        }
    }

    /**
     * Constructs a new instance of StandardCheckInfo with the default execution topology (ON_PRIMARY).
     *
     * @param checkName     the name of the check; must not be blank and will be converted to the upper case
     * @param sqlQuery      the SQL query associated with the check; must not be blank
     * @param queryExecutor the query executor for executing the SQL query; must not be null
     * @param runtimeCheck  whether this is a runtime check; required to be true if the execution topology is ACROSS_CLUSTER
     */
    protected StandardCheckInfo(final String checkName,
                                final String sqlQuery,
                                final QueryExecutor queryExecutor,
                                final boolean runtimeCheck) {
        this(checkName, ExecutionTopology.ON_PRIMARY, sqlQuery, queryExecutor, runtimeCheck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSqlQuery() {
        return sqlQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRuntime() {
        return runtimeCheck;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return checkName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionTopology getExecutionTopology() {
        return executionTopology;
    }

    /**
     * Creates a {@code CheckInfo} instance for a remaining percentage-based check using the specified check name.
     * The SQL query is automatically fetched based on the check name.
     *
     * @param checkName the name of the check; must not be blank
     * @return a new {@code CheckInfo} instance configured for remaining percentage-based checks with the provided check name
     */
    static CheckInfo ofRemainingPercentage(final String checkName) {
        return new StandardCheckInfo(checkName, SqlQueryReader.getQueryForCheck(checkName), QueryExecutors::executeQueryWithRemainingPercentageThreshold, true);
    }

    /**
     * Creates a {@code CheckInfo} instance specifically for identifying and handling bloat-related checks
     * using the provided check name.
     * The SQL query is automatically fetched based on the check name.
     *
     * @param checkName the name of the check; must not be blank.
     * @return a new {@code CheckInfo} instance configured for bloat-related checks with the specified check name.
     */
    static CheckInfo ofBloat(final String checkName) {
        return new StandardCheckInfo(checkName, SqlQueryReader.getQueryForCheck(checkName), QueryExecutors::executeQueryWithBloatThreshold, true);
    }

    /**
     * Creates a {@code CheckInfo} instance for a {@code ACROSS_CLUSTER} check using the specified check name.
     * The SQL query is automatically fetched based on the check name.
     *
     * @param checkName the name of the check; must not be blank
     * @return a new {@code CheckInfo} instance configured for execution across the cluster with the given check name
     */
    static CheckInfo ofCluster(final String checkName) {
        return new StandardCheckInfo(checkName, ExecutionTopology.ACROSS_CLUSTER, SqlQueryReader.getQueryForCheck(checkName), QueryExecutors::executeQueryWithSchema, true);
    }

    /**
     * Creates a {@code CheckInfo} instance for a static check using the provided check name.
     * The SQL query is automatically fetched based on the check name.
     *
     * @param checkName the name of the check; must not be blank
     * @return a new {@code CheckInfo} instance configured with the specified check name and its associated SQL query
     */
    static CheckInfo ofStatic(final String checkName) {
        return ofStatic(checkName, SqlQueryReader.getQueryForCheck(checkName));
    }

    /**
     * Creates a {@code CheckInfo} instance with the given name and SQL query for a static check.
     *
     * @param checkName the name of the check; must not be blank
     * @param sqlQuery  the SQL query to be executed for the check; must not be blank
     * @return a new {@code CheckInfo} instance configured with the specified check name and SQL query
     */
    public static CheckInfo ofStatic(final String checkName, final String sqlQuery) {
        return new StandardCheckInfo(checkName, sqlQuery, QueryExecutors::executeQueryWithSchema, false);
    }
}
