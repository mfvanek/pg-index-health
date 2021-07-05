/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A representation of foreign key in a database.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 */
@Immutable
public class ForeignKey implements TableNameAware {

    private final String tableName;
    private final String constraintName;
    private final List<String> columnsInConstraint;

    private ForeignKey(@Nonnull String tableName,
                       @Nonnull String constraintName,
                       @Nonnull List<String> columnsInConstraint) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.constraintName = Validators.notBlank(constraintName, "constraintName");
        this.columnsInConstraint = Collections.unmodifiableList(
                new ArrayList<>(Validators.validateThatNotEmpty(columnsInConstraint)));
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
     * Gets column names of foreign key constraint.
     *
     * @return column names of foreign key constraint
     */
    @Nonnull
    public List<String> getColumnsInConstraint() {
        return columnsInConstraint;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ForeignKey)) {
            return false;
        }

        final ForeignKey that = (ForeignKey) o;
        return Objects.equals(tableName, that.tableName) &&
                Objects.equals(constraintName, that.constraintName);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(tableName, constraintName);
    }

    @Override
    public String toString() {
        return ForeignKey.class.getSimpleName() + '{' +
                "tableName='" + tableName + '\'' +
                ", constraintName='" + constraintName + '\'' +
                ", columnsInConstraint=" + columnsInConstraint +
                '}';
    }

    @Nonnull
    public static ForeignKey of(@Nonnull String tableName,
                                @Nonnull String constraintName,
                                @Nonnull List<String> columnsInConstraint) {
        return new ForeignKey(tableName, constraintName, columnsInConstraint);
    }

    @Nonnull
    public static ForeignKey ofColumn(@Nonnull String tableName,
                                      @Nonnull String constraintName,
                                      @Nonnull String columnName) {
        return new ForeignKey(tableName, constraintName,
                Collections.singletonList(Validators.notBlank(columnName, "columnName")));
    }
}
