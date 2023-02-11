/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.extractors;

import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.model.table.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;

/**
 * A mapper from raw data to {@link Table} model.
 *
 * @author Ivan Vahrushev
 * @since 0.6.1
 */
public class TableExtractor implements ResultSetExtractor<Table> {

    private TableExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Table extractData(@Nonnull final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString("table_name");
        final long tableSize = resultSet.getLong("table_size");
        return Table.of(tableName, tableSize);
    }

    @Nonnull
    public static ResultSetExtractor<Table> of() {
        return new TableExtractor();
    }
}
