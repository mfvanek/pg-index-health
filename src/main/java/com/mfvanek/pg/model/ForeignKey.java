/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ForeignKey implements TableAware {

    private final String tableName;
    private final String columnsInConstraint;
    private final String constraintName;
    private final String constraintDefinition;

    private ForeignKey(@Nonnull String tableName,
                       @Nonnull String columnsInConstraint,
                       @Nonnull String constraintName,
                       @Nonnull String constraintDefinition) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.columnsInConstraint = Objects.requireNonNull(columnsInConstraint);
        this.constraintName = Objects.requireNonNull(constraintName);
        this.constraintDefinition = Objects.requireNonNull(constraintDefinition);
    }

    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Nonnull
    public String getColumnsInConstraint() {
        return columnsInConstraint;
    }

    @Nonnull
    public String getConstraintName() {
        return constraintName;
    }

    @Nonnull
    public String getConstraintDefinition() {
        return constraintDefinition;
    }

    @Override
    public String toString() {
        return ForeignKey.class.getSimpleName() + "{" +
                "tableName=\"" + tableName + "\'" +
                ", columnsInConstraint=" + columnsInConstraint +
                ", constraintName=" + constraintName +
                ", constraintDefinition=" + constraintDefinition +
                "}";
    }

    public static ForeignKey of(@Nonnull String tableName,
                                @Nonnull String columnsInConstraint,
                                @Nonnull String constraintName,
                                @Nonnull String constraintDefinition) {
        return new ForeignKey(tableName, columnsInConstraint, constraintName, constraintDefinition);
    }
}
