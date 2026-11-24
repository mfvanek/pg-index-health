/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.column.PgColumnType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;

/**
 * An immutable representation of a foreign key in a database.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 * @see Constraint
 */
public final class ForeignKey implements DbObject, ConstraintNameAware, ColumnsAware, Comparable<ForeignKey> {

    /**
     * Represents the name of the field that defines a database constraint.
     */
    public static final String CONSTRAINT_FIELD = "constraint";

    private final Constraint constraint;
    private final List<Column> columns;

    private ForeignKey(final Constraint constraint,
                       final List<Column> columns) {
        this.constraint = Objects.requireNonNull(constraint, CONSTRAINT_FIELD + " cannot be null");
        if (this.constraint.getConstraintType() != ConstraintType.FOREIGN_KEY) {
            throw new IllegalArgumentException(CONSTRAINT_FIELD + " must be foreign key");
        }
        final List<Column> defensiveCopy = List.copyOf(Objects.requireNonNull(columns, COLUMNS_FIELD + " cannot be null"));
        Validators.validateThatNotEmpty(defensiveCopy);
        Validators.validateThatTableIsTheSame(constraint.getTableName(), defensiveCopy);
        this.columns = defensiveCopy;
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
     * Retrieves the constraint associated with this foreign key.
     *
     * @return the constraint object associated with the foreign key
     * @author Ivan Vakhrushev
     * @since 0.20.3
     */
    public Constraint toConstraint() {
        return constraint;
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
        return List.copyOf(columns);
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
            ", " + COLUMNS_FIELD + '=' + columns +
            '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final ForeignKey other) {
        Objects.requireNonNull(other, "other cannot be null");
        return constraint.compareTo(other.constraint);
    }

    private static Constraint asConstraint(final String tableName,
                                           final String constraintName) {
        return Constraint.ofType(tableName, constraintName, ConstraintType.FOREIGN_KEY);
    }

    private static Constraint asConstraint(final PgContext ctx,
                                           final String tableName,
                                           final String constraintName) {
        return Constraint.ofType(ctx, tableName, constraintName, ConstraintType.FOREIGN_KEY);
    }

    /**
     * Creates a new {@code ForeignKey} object with the specified {@code Constraint} and associated columns.
     *
     * @param constraint the underlying constraint to be associated with the foreign key; must be non-null.
     * @param columns    a list of columns that are part of the constraint; must be non-null and non-empty.
     * @return a new instance of {@code ForeignKey}.
     * @author Ivan Vakhrushev
     * @since 0.20.3
     */
    public static ForeignKey of(final Constraint constraint,
                                final List<Column> columns) {
        return new ForeignKey(constraint, columns);
    }

    /**
     * Constructs a {@code ForeignKey} object with given columns.
     *
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columns        a list of columns that are included in constraint; should be non-empty.
     * @return {@code ForeignKey}
     */
    public static ForeignKey of(final String tableName,
                                final String constraintName,
                                final List<Column> columns) {
        return of(asConstraint(tableName, constraintName), columns);
    }

    /**
     * Constructs a {@code ForeignKey} object with given columns and context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columns        a list of columns that are included in constraint; should be non-empty.
     * @return {@code ForeignKey}
     * @since 0.14.5
     */
    public static ForeignKey of(final PgContext ctx,
                                final String tableName,
                                final String constraintName,
                                final List<Column> columns) {
        return of(asConstraint(ctx, tableName, constraintName), columns);
    }

    /**
     * Constructs a {@code ForeignKey} object with given {@code Constraint} and columns.
     *
     * @param constraint   underlying constraint that is included in a foreign key; must be non-null.
     * @param column       the first column that is included in constraint; must be non-null.
     * @param otherColumns other columns that are included in constraint; must not contain null elements.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey of(final Constraint constraint,
                                final Column column,
                                final Column... otherColumns) {
        return of(constraint, Validators.columnsNotNull(column, otherColumns));
    }

    /**
     * Constructs a {@code ForeignKey} object with given columns and context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param column         the first column that is included in constraint; must be non-null.
     * @param otherColumns   other columns that are included in constraint; must not contain null elements.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey of(final PgContext ctx,
                                final String tableName,
                                final String constraintName,
                                final Column column,
                                final Column... otherColumns) {
        return of(asConstraint(ctx, tableName, constraintName), column, otherColumns);
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @param columnType     type of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNotNull(final PgContext ctx,
                                       final String tableName,
                                       final String constraintName,
                                       final String columnName,
                                       final String columnType) {
        return of(ctx, tableName, constraintName, Column.ofNotNull(ctx, tableName, columnName, columnType));
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @param columnType     type of column that is included in constraint; must be non-null.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNotNull(final PgContext ctx,
                                       final String tableName,
                                       final String constraintName,
                                       final String columnName,
                                       final PgColumnType columnType) {
        return ofNotNull(ctx, tableName, constraintName, columnName, Validators.columnTypeNotNull(columnType));
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @param columnType     type of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullable(final PgContext ctx,
                                        final String tableName,
                                        final String constraintName,
                                        final String columnName,
                                        final String columnType) {
        return of(ctx, tableName, constraintName, Column.ofNullable(ctx, tableName, columnName, columnType));
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @param columnType     type of column that is included in constraint; must be non-null.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullable(final PgContext ctx,
                                        final String tableName,
                                        final String constraintName,
                                        final String columnName,
                                        final PgColumnType columnType) {
        return ofNullable(ctx, tableName, constraintName, columnName, Validators.columnTypeNotNull(columnType));
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code bigint} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofBigint(final PgContext ctx,
                                      final String tableName,
                                      final String constraintName,
                                      final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.BIGINT.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code bigint} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableBigint(final PgContext ctx,
                                              final String tableName,
                                              final String constraintName,
                                              final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.BIGINT.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code integer} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofInteger(final PgContext ctx,
                                       final String tableName,
                                       final String constraintName,
                                       final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.INTEGER.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code integer} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableInteger(final PgContext ctx,
                                               final String tableName,
                                               final String constraintName,
                                               final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.INTEGER.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code character varying} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofVarchar(final PgContext ctx,
                                       final String tableName,
                                       final String constraintName,
                                       final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.VARCHAR.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code character varying} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableVarchar(final PgContext ctx,
                                               final String tableName,
                                               final String constraintName,
                                               final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.VARCHAR.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code text} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofText(final PgContext ctx,
                                    final String tableName,
                                    final String constraintName,
                                    final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.TEXT.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code text} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableText(final PgContext ctx,
                                            final String tableName,
                                            final String constraintName,
                                            final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.TEXT.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code character} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofCharacter(final PgContext ctx,
                                         final String tableName,
                                         final String constraintName,
                                         final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.CHARACTER.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code character} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableCharacter(final PgContext ctx,
                                                 final String tableName,
                                                 final String constraintName,
                                                 final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.CHARACTER.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code boolean} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofBoolean(final PgContext ctx,
                                       final String tableName,
                                       final String constraintName,
                                       final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.BOOLEAN.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code boolean} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableBoolean(final PgContext ctx,
                                               final String tableName,
                                               final String constraintName,
                                               final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.BOOLEAN.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code timestamp without time zone} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofTimestamp(final PgContext ctx,
                                         final String tableName,
                                         final String constraintName,
                                         final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.TIMESTAMP.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code timestamp without time zone} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableTimestamp(final PgContext ctx,
                                                 final String tableName,
                                                 final String constraintName,
                                                 final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.TIMESTAMP.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code timestamp with time zone} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofTimestamptz(final PgContext ctx,
                                           final String tableName,
                                           final String constraintName,
                                           final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.TIMESTAMPTZ.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code timestamp with time zone} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableTimestamptz(final PgContext ctx,
                                                   final String tableName,
                                                   final String constraintName,
                                                   final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.TIMESTAMPTZ.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with not null column of {@code uuid} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofUuid(final PgContext ctx,
                                    final String tableName,
                                    final String constraintName,
                                    final String columnName) {
        return ofNotNull(ctx, tableName, constraintName, columnName, PgColumnType.UUID.getColumnType());
    }

    /**
     * Constructs a {@code ForeignKey} object with nullable column of {@code uuid} type and given context.
     *
     * @param ctx            the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param columnName     name of column that is included in constraint; should be non-blank.
     * @return {@code ForeignKey}
     * @since 0.51.0
     */
    public static ForeignKey ofNullableUuid(final PgContext ctx,
                                            final String tableName,
                                            final String constraintName,
                                            final String columnName) {
        return ofNullable(ctx, tableName, constraintName, columnName, PgColumnType.UUID.getColumnType());
    }
}
