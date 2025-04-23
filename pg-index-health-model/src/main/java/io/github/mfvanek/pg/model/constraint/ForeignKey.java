/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of foreign key in a database.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 * @see Constraint
 */
@Immutable
public final class ForeignKey extends Constraint implements ColumnsAware {

    private final List<Column> columnsInConstraint;

    private ForeignKey(@Nonnull final String tableName,
                       @Nonnull final String constraintName,
                       @Nonnull final List<Column> columnsInConstraint) {
        super(tableName, constraintName, ConstraintType.FOREIGN_KEY);
        final List<Column> defensiveCopy = List.copyOf(Objects.requireNonNull(columnsInConstraint, "columnsInConstraint cannot be null"));
        Validators.validateThatNotEmpty(defensiveCopy);
        Validators.validateThatTableIsTheSame(tableName, defensiveCopy);
        this.columnsInConstraint = defensiveCopy;
    }

    /**
     * Retrieves columns of foreign key constraint (one or more).
     *
     * @return columns of foreign key constraint
     * @see Column
     * @since 0.14.6
     */
    @Nonnull
    @Override
    public List<Column> getColumns() {
        return columnsInConstraint;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return ForeignKey.class.getSimpleName() + '{' +
            innerToString() +
            ", columnsInConstraint=" + columnsInConstraint +
            '}';
    }

    /**
     * Constructs a {@code ForeignKey} object with given columns.
     *
     * @param tableName           table name; should be non-blank.
     * @param constraintName      constraint name; should be non-blank.
     * @param columnsInConstraint list of columns that are included in constraint; should be non-empty.
     * @return {@code ForeignKey}
     */
    @Nonnull
    public static ForeignKey of(@Nonnull final String tableName,
                                @Nonnull final String constraintName,
                                @Nonnull final List<Column> columnsInConstraint) {
        return new ForeignKey(tableName, constraintName, columnsInConstraint);
    }

    /**
     * Constructs a {@code ForeignKey} object with given columns and context.
     *
     * @param pgContext           the schema context to enrich table name; must be non-null.
     * @param tableName           table name; should be non-blank.
     * @param constraintName      constraint name; should be non-blank.
     * @param columnsInConstraint list of columns that are included in constraint; should be non-empty.
     * @return {@code ForeignKey}
     * @since 0.14.5
     */
    @Nonnull
    public static ForeignKey of(@Nonnull final PgContext pgContext,
                                @Nonnull final String tableName,
                                @Nonnull final String constraintName,
                                @Nonnull final List<Column> columnsInConstraint) {
        return new ForeignKey(PgContext.enrichWith(tableName, pgContext), constraintName, columnsInConstraint);
    }

    /**
     * Constructs a {@code ForeignKey} object with given {@code Column}.
     *
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param column         column that is included in constraint.
     * @return {@code ForeignKey}
     */
    @Nonnull
    public static ForeignKey ofColumn(@Nonnull final String tableName,
                                      @Nonnull final String constraintName,
                                      @Nonnull final Column column) {
        return new ForeignKey(tableName, constraintName, List.of(Objects.requireNonNull(column, "column cannot be null")));
    }

    /**
     * Constructs a {@code ForeignKey} object with given {@code Column} and context.
     *
     * @param pgContext      the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param column         column that is included in constraint.
     * @return {@code ForeignKey}
     * @since 0.15.0
     */
    @Nonnull
    public static ForeignKey ofColumn(@Nonnull final PgContext pgContext,
                                      @Nonnull final String tableName,
                                      @Nonnull final String constraintName,
                                      @Nonnull final Column column) {
        return ofColumn(PgContext.enrichWith(tableName, pgContext), constraintName, column);
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column.
     *
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     */
    @Nonnull
    public static ForeignKey ofNotNullColumn(@Nonnull final String tableName,
                                             @Nonnull final String constraintName,
                                             @Nonnull final String columnName) {
        return ofColumn(tableName, constraintName, Column.ofNotNull(tableName, columnName));
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column and given context.
     *
     * @param pgContext      the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.14.5
     */
    @Nonnull
    public static ForeignKey ofNotNullColumn(@Nonnull final PgContext pgContext,
                                             @Nonnull final String tableName,
                                             @Nonnull final String constraintName,
                                             @Nonnull final String columnName) {
        return ofColumn(pgContext, tableName, constraintName, Column.ofNotNull(pgContext, tableName, columnName));
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column.
     *
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     */
    @Nonnull
    public static ForeignKey ofNullableColumn(@Nonnull final String tableName,
                                              @Nonnull final String constraintName,
                                              @Nonnull final String columnName) {
        return ofColumn(tableName, constraintName, Column.ofNullable(tableName, columnName));
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column and given context.
     *
     * @param pgContext      the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.14.5
     */
    @Nonnull
    public static ForeignKey ofNullableColumn(@Nonnull final PgContext pgContext,
                                              @Nonnull final String tableName,
                                              @Nonnull final String constraintName,
                                              @Nonnull final String columnName) {
        return ofColumn(pgContext, tableName, constraintName, Column.ofNullable(pgContext, tableName, columnName));
    }
}
