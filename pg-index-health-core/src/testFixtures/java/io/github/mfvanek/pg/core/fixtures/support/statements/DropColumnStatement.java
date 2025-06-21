/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Locale;

public class DropColumnStatement extends AbstractDbStatement {

    private final String tableName;
    private final String columnName;

    public DropColumnStatement(final String tableName, final String columnName) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.columnName = Validators.notBlank(columnName, "columnName");
    }

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            String.format(Locale.ROOT, "alter table if exists {schemaName}.%s drop column %s", tableName, columnName)
        );
    }
}
