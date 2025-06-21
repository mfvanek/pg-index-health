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
 * A mapper from raw data to domain model.
 *
 * @param <T> any type represents an object in a database
 * @author Ivan Vakhrushev
 */
@FunctionalInterface
public interface ResultSetExtractor<T> {

    /**
     * Converts a row from database to an arbitrary domain model.
     *
     * @param resultSet the ResultSet to extract data from
     * @return an arbitrary result object
     * @throws SQLException if an SQLException is encountered getting column values or navigating
     */
    T extractData(ResultSet resultSet) throws SQLException;
}
