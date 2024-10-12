/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.extractors;

import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.utils.ColumnsInForeignKeyParser;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.checks.extractors.TableExtractor.TABLE_NAME;

/**
 * A mapper from raw data to {@link ForeignKey} model.
 *
 * @author Ivan Vahrushev
 * @since 0.13.1
 */
public class ForeignKeyExtractor implements ResultSetExtractor<ForeignKey> {

    public static final String CONSTRAINT_NAME = "constraint_name";

    private final String prefix;

    private ForeignKeyExtractor(@Nonnull final String prefix) {
        this.prefix = Objects.requireNonNull(prefix, "prefix cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ForeignKey extractData(@Nonnull final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TABLE_NAME);
        final String constraintName = resultSet.getString(getConstraintNameField());
        final Array columnsArray = resultSet.getArray(getColumnsField());
        final String[] rawColumns = (String[]) columnsArray.getArray();
        final List<Column> columns = ColumnsInForeignKeyParser.parseRawColumnData(tableName, rawColumns);
        return ForeignKey.of(tableName, constraintName, columns);
    }

    @Nonnull
    private String getConstraintNameField() {
        if (!prefix.isBlank()) {
            return prefix + "_" + CONSTRAINT_NAME;
        }
        return CONSTRAINT_NAME;
    }

    @Nonnull
    private String getColumnsField() {
        if (!prefix.isBlank()) {
            return prefix + "_constraint_columns";
        }
        return "columns";
    }

    /**
     * Creates default {@code ForeignKeyExtractor} instance.
     *
     * @return {@code ForeignKeyExtractor} instance
     */
    @Nonnull
    public static ResultSetExtractor<ForeignKey> ofDefault() {
        return new ForeignKeyExtractor("");
    }

    /**
     * Creates {@code ForeignKeyExtractor} instance for duplicated/intersected constraint fields with given prefix.
     *
     * @param prefix given prefix; must be non-null
     * @return {@code ForeignKeyExtractor} instance
     */
    @Nonnull
    public static ResultSetExtractor<ForeignKey> withPrefix(@Nonnull final String prefix) {
        return new ForeignKeyExtractor(prefix);
    }
}
