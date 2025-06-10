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
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;

/**
 * An immutable representation of foreign key in a database.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 * @see Constraint
 */
public final class ForeignKey implements DbObject, ConstraintNameAware, ColumnsAware {

    private final Constraint constraint;
    private final List<Column> columnsInConstraint;

    private ForeignKey(final Constraint constraint,
                       final List<Column> columnsInConstraint) {
        this.constraint = Objects.requireNonNull(constraint, "constraint cannot be null");
        if (this.constraint.getConstraintType() != ConstraintType.FOREIGN_KEY) {
            throw new IllegalArgumentException("constraint must be foreign key");
        }
        final List<Column> defensiveCopy = List.copyOf(Objects.requireNonNull(columnsInConstraint, "columnsInConstraint cannot be null"));
        Validators.validateThatNotEmpty(defensiveCopy);
        Validators.validateThatTableIsTheSame(constraint.getTableName(), defensiveCopy);
        this.columnsInConstraint = defensiveCopy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConstraintName() {
        return constraint.getConstraintName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstraintType getConstraintType() {
        return constraint.getConstraintType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return constraint.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return constraint.getObjectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return constraint.getTableName();
    }

    /**
     * Retrieves columns of foreign key constraint (one or more).
     *
     * @return columns of foreign key constraint
     * @see Column
     * @since 0.14.6
     */
    @Override
    public List<ColumnNameAware> getColumns() {
        return List.copyOf(columnsInConstraint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final ForeignKey that)) {
            return false;
        }

        return Objects.equals(constraint, that.constraint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(constraint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ForeignKey.class.getSimpleName() + '{' +
            constraint.innerToString() +
            ", columnsInConstraint=" + columnsInConstraint +
            '}';
    }

    private static Constraint toConstraint(final String tableName,
                                           final String constraintName) {
        return Constraint.ofType(tableName, constraintName, ConstraintType.FOREIGN_KEY);
    }

    private static Constraint toConstraint(final PgContext pgContext,
                                           final String tableName,
                                           final String constraintName) {
        return Constraint.ofType(pgContext, tableName, constraintName, ConstraintType.FOREIGN_KEY);
    }

    /**
     * Constructs a {@code ForeignKey} object with given columns.
     *
     * @param tableName           table name; should be non-blank.
     * @param constraintName      constraint name; should be non-blank.
     * @param columnsInConstraint list of columns that are included in constraint; should be non-empty.
     * @return {@code ForeignKey}
     */
    public static ForeignKey of(final String tableName,
                                final String constraintName,
                                final List<Column> columnsInConstraint) {
        return new ForeignKey(toConstraint(tableName, constraintName), columnsInConstraint);
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
    public static ForeignKey of(final PgContext pgContext,
                                final String tableName,
                                final String constraintName,
                                final List<Column> columnsInConstraint) {
        return new ForeignKey(toConstraint(pgContext, tableName, constraintName), columnsInConstraint);
    }

    /**
     * Constructs a {@code ForeignKey} object with given {@code Constraint} and {@code Column}.
     *
     * @param constraint underlying constraint that is included in foreign key; must be non-null.
     * @param column     column that is included in constraint; must be non-null.
     * @return {@code ForeignKey}
     * @since 0.15.0
     */
    public static ForeignKey ofColumn(final Constraint constraint,
                                      final Column column) {
        return new ForeignKey(constraint, List.of(Objects.requireNonNull(column, "column cannot be null")));
    }

    /**
     * Constructs a {@code ForeignKey} object with given {@code Column}.
     *
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param column         column that is included in constraint; must be non-null.
     * @return {@code ForeignKey}
     */
    public static ForeignKey ofColumn(final String tableName,
                                      final String constraintName,
                                      final Column column) {
        return ofColumn(toConstraint(tableName, constraintName), column);
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
    public static ForeignKey ofColumn(final PgContext pgContext,
                                      final String tableName,
                                      final String constraintName,
                                      final Column column) {
        return ofColumn(toConstraint(pgContext, tableName, constraintName), column);
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column.
     *
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     */
    public static ForeignKey ofNotNullColumn(final String tableName,
                                             final String constraintName,
                                             final String columnName) {
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
    public static ForeignKey ofNotNullColumn(final PgContext pgContext,
                                             final String tableName,
                                             final String constraintName,
                                             final String columnName) {
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
    public static ForeignKey ofNullableColumn(final String tableName,
                                              final String constraintName,
                                              final String columnName) {
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
    public static ForeignKey ofNullableColumn(final PgContext pgContext,
                                              final String tableName,
                                              final String constraintName,
                                              final String columnName) {
        return ofColumn(pgContext, tableName, constraintName, Column.ofNullable(pgContext, tableName, columnName));
    }
}
