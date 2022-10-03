/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.Validators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of foreign key in a database.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 */
@Immutable
public class ForeignKey implements DbObject, TableNameAware {

    private final String tableName;
    private final String constraintName;
    private final List<Column> columnsInConstraint;

    private ForeignKey(@Nonnull final String tableName,
                       @Nonnull final String constraintName,
                       @Nonnull final List<Column> columnsInConstraint) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.constraintName = Validators.notBlank(constraintName, "constraintName");
        final List<Column> defensiveCopy = new ArrayList<>(
                Objects.requireNonNull(columnsInConstraint, "columnsInConstraint"));
        Validators.validateThatNotEmpty(defensiveCopy);
        Validators.validateThatTableIsTheSame(tableName, defensiveCopy);
        this.columnsInConstraint = Collections.unmodifiableList(defensiveCopy);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getName() {
        return getConstraintName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets the name of foreign key constraint.
     *
     * @return the name of foreign key
     */
    @Nonnull
    public String getConstraintName() {
        return constraintName;
    }

    /**
     * Gets columns of foreign key constraint.
     *
     * @return columns of foreign key constraint
     * @see Column
     */
    @Nonnull
    public List<Column> getColumnsInConstraint() {
        return columnsInConstraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ForeignKey)) {
            return false;
        }

        final ForeignKey that = (ForeignKey) other;
        return Objects.equals(tableName, that.tableName) &&
                Objects.equals(constraintName, that.constraintName) &&
                Objects.equals(columnsInConstraint, that.columnsInConstraint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(tableName, constraintName, columnsInConstraint);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return ForeignKey.class.getSimpleName() + '{' +
                "tableName='" + tableName + '\'' +
                ", constraintName='" + constraintName + '\'' +
                ", columnsInConstraint=" + columnsInConstraint +
                '}';
    }

    @Nonnull
    public static ForeignKey of(@Nonnull final String tableName,
                                @Nonnull final String constraintName,
                                @Nonnull final List<Column> columnsInConstraint) {
        return new ForeignKey(tableName, constraintName, columnsInConstraint);
    }

    @Nonnull
    public static ForeignKey ofColumn(@Nonnull final String tableName,
                                      @Nonnull final String constraintName,
                                      @Nonnull final Column column) {
        return new ForeignKey(tableName, constraintName,
                Collections.singletonList(Objects.requireNonNull(column, "column")));
    }

    @Nonnull
    public static ForeignKey ofNotNullColumn(@Nonnull final String tableName,
                                             @Nonnull final String constraintName,
                                             @Nonnull final String columnName) {
        return ofColumn(tableName, constraintName, Column.ofNotNull(tableName, columnName));
    }

    @Nonnull
    public static ForeignKey ofNullableColumn(@Nonnull final String tableName,
                                              @Nonnull final String constraintName,
                                              @Nonnull final String columnName) {
        return ofColumn(tableName, constraintName, Column.ofNullable(tableName, columnName));
    }
}
