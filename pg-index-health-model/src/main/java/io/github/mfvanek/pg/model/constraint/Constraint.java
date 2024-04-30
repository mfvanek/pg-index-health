/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public class Constraint implements DbObject, TableNameAware {

    private final String tableName;
    private final String constraintName;
    private final ConstraintType constraintType;

    private Constraint(
            @Nonnull String tableName,
            @Nonnull String constraintName,
            @Nonnull ConstraintType constraintType) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.constraintName = Validators.notBlank(constraintName, "constraintName");
        this.constraintType = constraintType;
    }

    @Nonnull
    @Override
    public String getName() {
        return getConstraintName();
    }

    @Nonnull
    @Override
    public String getTableName() {
        return tableName;
    }

    @Nonnull
    public String getConstraintName() {
        return constraintName;
    }

    @Nonnull
    public ConstraintType getConstraintType() {
        return constraintType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Constraint)) return false;

        Constraint that = (Constraint) o;

        if (!tableName.equals(that.tableName)) return false;
        if (!constraintName.equals(that.constraintName)) return false;
        return constraintType == that.constraintType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, constraintName, constraintType);
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "tableName='" + tableName + '\'' +
                ", constraintName='" + constraintName + '\'' +
                ", constraintType=" + constraintType +
                '}';
    }

    @Nonnull
    public static Constraint of(
            @Nonnull String tableName,
            @Nonnull String constraintName,
            @Nonnull ConstraintType constraintType) {
        return new Constraint(tableName, constraintName, constraintType);
    }
}
