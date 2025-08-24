/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Locale;
import java.util.Objects;

/**
 * An immutable representation of constraint in a database.
 *
 * @author Blohny
 * @see TableNameAware
 * @since 0.11.0
 */
public final class Constraint implements DbObject, ConstraintNameAware {

    /**
     * A constant representing the field name for the type of constraint.
     */
    public static final String CONSTRAINT_TYPE_FIELD = "constraintType";

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
    private Constraint(
        final String tableName,
        final String constraintName,
        final ConstraintType constraintType) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.constraintName = Validators.notBlank(constraintName, CONSTRAINT_NAME_FIELD);
        this.constraintType = Objects.requireNonNull(constraintType, CONSTRAINT_TYPE_FIELD + " cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getConstraintName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.CONSTRAINT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConstraintName() {
        return constraintName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstraintType getConstraintType() {
        return constraintType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final Constraint that)) {
            return false;
        }

        return Objects.equals(tableName, that.tableName) &&
            Objects.equals(constraintName, that.constraintName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(tableName, constraintName);
    }

    /**
     * An auxiliary utility method for implementing {@code toString()} in child classes.
     *
     * @return string representation of the internal fields of this class
     */
    String innerToString() {
        return TABLE_NAME_FIELD + "='" + tableName + '\'' +
            ", " + CONSTRAINT_NAME_FIELD + "='" + constraintName + '\'';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Constraint.class.getSimpleName() + '{' +
            innerToString() +
            ", " + CONSTRAINT_TYPE_FIELD + '=' + constraintType +
            '}';
    }

    /**
     * Builds and returns sql query to validate current constraint.
     *
     * @return sql query to validate current constraint
     * @see <a href="https://www.postgresql.org/docs/current/sql-altertable.html#SQL-ALTERTABLE-DESC-VALIDATE-CONSTRAINT">VALIDATE CONSTRAINT</a>
     */
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
    public static Constraint ofType(final String tableName,
                                    final String constraintName,
                                    final ConstraintType constraintType) {
        return new Constraint(tableName, constraintName, constraintType);
    }

    /**
     * Constructs a {@code Constraint} object with given {@code ConstraintType} and context.
     *
     * @param pgContext      the schema context to enrich table name; must be non-null.
     * @param tableName      table name; should be non-blank.
     * @param constraintName constraint name; should be non-blank.
     * @param constraintType constraint type; should be non-null.
     * @return {@code Constraint}
     * @since 0.14.3
     */
    public static Constraint ofType(final PgContext pgContext,
                                    final String tableName,
                                    final String constraintName,
                                    final ConstraintType constraintType) {
        return ofType(PgContext.enrichWith(tableName, pgContext), constraintName, constraintType);
    }
}
