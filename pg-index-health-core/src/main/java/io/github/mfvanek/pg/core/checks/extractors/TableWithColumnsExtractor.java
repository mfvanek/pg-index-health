/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.core.utils.ColumnsDataParser;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithColumns;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.core.checks.extractors.TableExtractor.TABLE_NAME;
import static io.github.mfvanek.pg.core.checks.extractors.TableExtractor.TABLE_SIZE;

/**
 * A mapper from raw data to {@link TableWithColumns} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.14.6
 */
public class TableWithColumnsExtractor implements ResultSetExtractor<TableWithColumns> {

    private TableWithColumnsExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public TableWithColumns extractData(@Nonnull final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TABLE_NAME);
        final long tableSize = resultSet.getLong(TABLE_SIZE);
        final Array columnsArray = resultSet.getArray("columns");
        final String[] rawColumns = (String[]) columnsArray.getArray();
        final List<Column> columns = ColumnsDataParser.parseRawColumnInTable(tableName, rawColumns);
        return TableWithColumns.of(Table.of(tableName, tableSize), columns);
    }

    /**
     * Creates {@code TableExtractor} instance.
     *
     * @return {@code TableExtractor} instance
     */
    @Nonnull
    public static ResultSetExtractor<TableWithColumns> of() {
        return new TableWithColumnsExtractor();
    }
}
