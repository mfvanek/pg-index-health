/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForeignKey implements TableNameAware {

    private final String tableName;
    private final String constraintName;
    private final List<String> columnsInConstraint;

    private ForeignKey(@Nonnull String tableName,
                       @Nonnull String constraintName,
                       @Nonnull List<String> columnsInConstraint) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.constraintName = Validators.notBlank(constraintName, "constraintName");
        this.columnsInConstraint = new ArrayList<>(Validators.validateThatNotEmpty(columnsInConstraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Nonnull
    public String getConstraintName() {
        return constraintName;
    }

    @Nonnull
    public List<String> getColumnsInConstraint() {
        return Collections.unmodifiableList(columnsInConstraint);
    }

    @Override
    public String toString() {
        return ForeignKey.class.getSimpleName() + '{' +
                "tableName='" + tableName + '\'' +
                ", constraintName='" + constraintName + '\'' +
                ", columnsInConstraint=" + columnsInConstraint +
                '}';
    }

    public static ForeignKey of(@Nonnull String tableName,
                                @Nonnull String constraintName,
                                @Nonnull List<String> columnsInConstraint) {
        return new ForeignKey(tableName, constraintName, columnsInConstraint);
    }
}
