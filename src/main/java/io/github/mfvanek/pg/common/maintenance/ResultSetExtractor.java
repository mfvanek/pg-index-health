/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;

/**
 * A mapper from raw data to domain model.
 *
 * @param <T> any type represents an object in a database
 *
 * @author Ivan Vahrushev
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
    @Nonnull
    T extractData(@Nonnull ResultSet resultSet) throws SQLException;
}
