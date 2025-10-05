/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.core.utils.ColumnsDataParser;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static io.github.mfvanek.pg.core.checks.extractors.TableExtractor.TABLE_NAME;

/**
 * A mapper from raw data to {@link ForeignKey} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.1
 */
public final class ForeignKeyExtractor implements ResultSetExtractor<ForeignKey> {

    /**
     * Represents the column name "constraint_name" in a ResultSet.
     * Used to extract the "constraint_name" field from query results
     * when extracting the {@code ForeignKey} model.
     */
    public static final String CONSTRAINT_NAME = "constraint_name";

    private final String prefix;

    private ForeignKeyExtractor(final String prefix) {
        this.prefix = Objects.requireNonNull(prefix, "prefix cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ForeignKey extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TABLE_NAME);
        final String constraintName = resultSet.getString(getConstraintNameField());
        final Array columnsArray = resultSet.getArray(getColumnsField());
        final String[] rawColumns = (String[]) columnsArray.getArray();
        final List<Column> columns = ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex(tableName, rawColumns);
        return ForeignKey.of(tableName, constraintName, columns);
    }

    private String getConstraintNameField() {
        if (!prefix.isBlank()) {
            return prefix + "_" + CONSTRAINT_NAME;
        }
        return CONSTRAINT_NAME;
    }

    private String getColumnsField() {
        if (!prefix.isBlank()) {
            return prefix + "_constraint_columns";
        }
        return ColumnsAware.COLUMNS_FIELD;
    }

    /**
     * Creates default {@code ForeignKeyExtractor} instance.
     *
     * @return {@code ForeignKeyExtractor} instance
     */
    public static ResultSetExtractor<ForeignKey> ofDefault() {
        return new ForeignKeyExtractor("");
    }

    /**
     * Creates {@code ForeignKeyExtractor} instance for duplicated/intersected constraint fields with given prefix.
     *
     * @param prefix given prefix; must be non-null
     * @return {@code ForeignKeyExtractor} instance
     */
    public static ResultSetExtractor<ForeignKey> withPrefix(final String prefix) {
        return new ForeignKeyExtractor(prefix);
    }
}
