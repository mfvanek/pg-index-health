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

import java.sql.SQLException;
import java.sql.Statement;

/**
 * A functional interface representing a database statement executor.
 * <p>
 * Implementations of this interface must define logic to execute database operations
 * using a provided {@link Statement}.
 * <p>
 * This interface is intended to be used with lambda expressions or method references.
 */
@FunctionalInterface
public interface DbStatement {

    /**
     * Executes the provided SQL {@link Statement}.
     * <p>
     * This method is intended to execute database operations using the
     * given {@link Statement} object. Implementations should define
     * the specific database logic to be executed.
     *
     * @param statement the {@link Statement} object used to execute the SQL operations.
     *                  Must not be null.
     * @throws SQLException if a database access error occurs or the SQL statement execution fails.
     */
    void execute(Statement statement) throws SQLException;
}
