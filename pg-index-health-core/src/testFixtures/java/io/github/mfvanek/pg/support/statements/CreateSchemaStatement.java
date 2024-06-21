/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;

public class CreateSchemaStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of("create schema if not exists {schemaName}");
    }

    @Override
    public void postExecute(@Nonnull final Statement statement, @Nonnull final String schemaName) throws SQLException {
        final String checkQuery = String.format(
            Locale.ROOT, "select exists(select 1 from information_schema.schemata where schema_name = '%s')", schemaName);
        try (ResultSet rs = statement.executeQuery(checkQuery)) {
            if (rs.next()) {
                final boolean schemaExists = rs.getBoolean(1);
                if (schemaExists) {
                    return;
                }
            }
            throw new IllegalStateException("Schema with name " + schemaName + " wasn't created");
        }
    }
}
