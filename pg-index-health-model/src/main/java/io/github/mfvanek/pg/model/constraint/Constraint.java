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
import io.github.mfvanek.pg.model.object.PgObjectType;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of constraint in a database.
 *
 * @author Blohny
 * @see TableNameAware
 * @since 0.11.0
 */
@Immutable
public class Constraint implements DbObject, TableNameAware {

    private final String tableName;
    private final String constraintName;
    private final ConstraintType constraintType;

    /**
     * Constructs a {@code Constraint} object with given {@code ConstraintType}.
     *
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param constraintType constraint type; should be non-null.
     */
    protected Constraint(
        @Nonnull final String tableName,
        @Nonnull final String constraintName,
        @Nonnull final ConstraintType constraintType) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.constraintName = Validators.notBlank(constraintName, "constraintName");
        this.constraintType = Objects.requireNonNull(constraintType, "constraintType cannot be null");
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
    @Nonnull
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.CONSTRAINT;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets the name of constraint.
     *
     * @return the name of constraint
     */
    @Nonnull
    public String getConstraintName() {
        return constraintName;
    }

    /**
     * Gets type of constraint.
     *
     * @return type of constraint
     * @see ConstraintType
     */
    @Nonnull
    public ConstraintType getConstraintType() {
        return constraintType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Constraint)) {
            return false;
        }

        final Constraint that = (Constraint) other;
        return Objects.equals(tableName, that.tableName) &&
            Objects.equals(constraintName, that.constraintName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(tableName, constraintName);
    }

    /**
     * An auxiliary utility method for implementing {@code toString()} in child classes.
     *
     * @return string representation of the internal fields of this class
     */
    @Nonnull
    final String innerToString() {
        return "tableName='" + tableName + '\'' +
            ", constraintName='" + constraintName + '\'';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Constraint.class.getSimpleName() + '{' +
            innerToString() +
            ", constraintType=" + constraintType +
            '}';
    }

    /**
     * Builds and returns sql query to validate current constraint.
     *
     * @return sql query to validate current constraint
     * @see <a href="https://www.postgresql.org/docs/current/sql-altertable.html#SQL-ALTERTABLE-DESC-VALIDATE-CONSTRAINT">VALIDATE CONSTRAINT</a>
     */
    @Nonnull
    public String getValidateSql() {
        return String.format(Locale.ROOT, "alter table %s validate constraint %s;", tableName, constraintName);
    }

    /**
     * Constructs a {@code Constraint} object with given {@code ConstraintType}.
     *
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param constraintType constraint type; should be non-null.
     * @return {@code Constraint}
     */
    @Nonnull
    public static Constraint ofType(@Nonnull final String tableName,
                                    @Nonnull final String constraintName,
                                    @Nonnull final ConstraintType constraintType) {
        return new Constraint(tableName, constraintName, constraintType);
    }
}
