/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to a domain model.
 *
 * @param <T> any type represents an object in a database
 * @author Ivan Vakhrushev
 */
@FunctionalInterface
public interface ResultSetExtractor<T> {

    /**
     * Converts the current row of the given {@code ResultSet} to an arbitrary domain model.
     *
     * @param resultSet the ResultSet to extract data from
     * @return an arbitrary result object
     * @throws SQLException if an SQLException is encountered getting column values or navigating
     */
    T extractData(ResultSet resultSet) throws SQLException;

    /**
     * Converts the current row of the given {@code ResultSet} to an arbitrary domain model.
     * <p>
     * This method is compatible with Spring Framework's {@code RowMapper}.
     *
     * @param rs      the {@code ResultSet} to extract data from
     * @param ignored the current row number in the {@code ResultSet}; will be ignored
     * @return an instance of the domain model representing the current row
     * @throws SQLException if an SQLException is encountered when accessing data from the {@code ResultSet}
     * @since 0.30.0
     */
    default T mapRow(final ResultSet rs, final int ignored) throws SQLException {
        return extractData(rs);
    }
}
