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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.annotation.Nonnull;

abstract class AbstractDbStatement implements DbStatement {

    protected final String schemaName;

    protected AbstractDbStatement(@Nonnull final String schemaName) {
        this.schemaName = Objects.requireNonNull(schemaName);
    }

    protected void throwExceptionIfTableDoesNotExist(@Nonnull final Statement statement, @Nonnull final String tableName) throws SQLException {
        final String checkQuery = String.format("select exists (%n" +
                "   select 1 %n" +
                "   from pg_catalog.pg_class c%n" +
                "   join pg_catalog.pg_namespace n on n.oid = c.relnamespace%n" +
                "   where n.nspname = '%s'%n" +
                "   and c.relname = '%s'%n" +
                "   and c.relkind = 'r'%n" +
                "   );", schemaName, tableName);
        try (ResultSet rs = statement.executeQuery(checkQuery)) {
            if (rs.next()) {
                final boolean schemaExists = rs.getBoolean(1);
                if (schemaExists) {
                    return;
                }
            }
            throw new IllegalStateException(String.format("Table with name '%s' in schema '%s' wasn't created", tableName, schemaName));
        }
    }
}
