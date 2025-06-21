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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

public class CreateSchemaStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of("create schema if not exists {schemaName}");
    }

    @Override
    public void postExecute(final Statement statement, final String schemaName) throws SQLException {
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
