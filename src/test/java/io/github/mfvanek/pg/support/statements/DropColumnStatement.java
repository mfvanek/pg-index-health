/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import io.github.mfvanek.pg.model.validation.Validators;

import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

public class DropColumnStatement extends AbstractDbStatement {

    private final String tableName;
    private final String columnName;

    public DropColumnStatement(@Nonnull final String schemaName, @Nonnull final String tableName, @Nonnull final String columnName) {
        super(schemaName);
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.columnName = Validators.notBlank(columnName, "columnName");
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("alter table if exists %s.%s drop column %s", schemaName, tableName, columnName));
    }
}
