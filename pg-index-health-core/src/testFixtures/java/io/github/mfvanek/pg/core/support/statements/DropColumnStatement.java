/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.support.statements;

import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import javax.annotation.Nonnull;

public class DropColumnStatement extends AbstractDbStatement {

    private final String tableName;
    private final String columnName;

    public DropColumnStatement(@Nonnull final String tableName, @Nonnull final String columnName) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.columnName = Validators.notBlank(columnName, "columnName");
    }

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of("alter table if exists {schemaName}." + tableName + " drop column " + columnName);
    }
}
