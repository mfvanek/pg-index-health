/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import io.github.mfvanek.pg.core.fixtures.support.SchemaNameHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

/**
 * AbstractDbStatement is an abstract base class that provides a framework for executing
 * database-related operations.
 * <p>
 * It implements the {@link DbStatement} interface and contains
 * functionality to execute SQL statements with schema name parameterization.
 * <p>
 * Subclasses must
 * implement the {@code getSqlToExecute()} method to provide the specific SQL statements to run.
 */
public abstract class AbstractDbStatement implements DbStatement {

    /**
     * Constructs a new instance of {@code AbstractDbStatement}.
     * <p>
     * This constructor initializes the base class for executing database statements
     * with the ability to handle SQL execution logic, manage placeholders, and define
     * post-execution behavior. Subclasses are expected to implement the necessary
     * functionality for specific database operations.
     */
    public AbstractDbStatement() {
    }

    /**
     * Validates the existence of a table within a specific schema in the database.
     * If the table does not exist, throws an {@link IllegalStateException}.
     *
     * @param statement  the {@link Statement} object used to execute the SQL query. Must not be null.
     * @param tableName  the name of the table to check for existence. Must not be null or empty.
     * @param schemaName the name of the schema where the table is expected to reside. Must not be null or empty.
     * @throws SQLException          if a database access error occurs while executing the query.
     * @throws IllegalStateException if the table does not exist in the specified schema.
     */
    protected void throwExceptionIfTableDoesNotExist(
        final Statement statement,
        final String tableName,
        final String schemaName
    ) throws SQLException {
        final String checkQuery = String.format(Locale.ROOT, """
            select exists (
                select 1
                from pg_catalog.pg_class c
                join pg_catalog.pg_namespace n on n.oid = c.relnamespace
                where
                    n.nspname = '%s'
                    and c.relname = '%s'
                    and c.relkind = 'r'
            );""", schemaName, tableName);
        try (ResultSet rs = statement.executeQuery(checkQuery)) {
            if (rs.next()) {
                final boolean schemaExists = rs.getBoolean(1);
                if (schemaExists) {
                    return;
                }
            }
            throw new IllegalStateException(
                String.format(Locale.ROOT, "Table with name '%s' in schema '%s' wasn't created", tableName, schemaName));
        }
    }

    /**
     * Provides a list of SQL statements to be executed, with placeholders that can be
     * substituted dynamically (e.g., schema name).
     * <p>
     * Subclasses must implement this method to supply specific SQL statements tailored
     * to their intended database operation.
     *
     * @return a list of SQL statements to execute, where each statement may include placeholders
     * for runtime substitution. Must not return {@code null}.
     */
    protected abstract List<String> getSqlToExecute();

    /**
     * Executes a series of SQL statements on the given {@link Statement} object, replacing placeholders
     * with the actual schema name, and performs post-execution logic.
     *
     * @param statement the {@link Statement} object used to execute the SQL statements. Must not be null.
     * @throws SQLException if a database access error occurs or the SQL statement is invalid.
     */
    @Override
    public void execute(final Statement statement) throws SQLException {
        final String schemaName = SchemaNameHolder.getSchemaName();
        for (final String sql : getSqlToExecute()) {
            statement.execute(sql.replace("{schemaName}", schemaName));
        }
        postExecute(statement, schemaName);
    }

    /**
     * This method is intended to be overridden by subclasses to perform any post-execution logic.
     *
     * @param statement  the {@link Statement} object used to execute the SQL statements. Must not be null.
     * @param schemaName the name of the schema used in the SQL statements. Must not be null.
     * @throws SQLException if a database access error occurs.
     */
    protected void postExecute(final Statement statement, final String schemaName) throws SQLException {
    }
}
