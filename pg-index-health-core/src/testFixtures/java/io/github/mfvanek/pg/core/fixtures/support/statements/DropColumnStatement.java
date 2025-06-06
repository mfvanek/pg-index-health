/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;

public class DropColumnStatement extends AbstractDbStatement {

    private final String tableName;
    private final String columnName;

    public DropColumnStatement(final String tableName, final String columnName) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.columnName = Validators.notBlank(columnName, "columnName");
    }

    @Override
    protected List<String> getSqlToExecute() {
        return List.of("alter table if exists {schemaName}." + tableName + " drop column " + columnName);
    }
}
