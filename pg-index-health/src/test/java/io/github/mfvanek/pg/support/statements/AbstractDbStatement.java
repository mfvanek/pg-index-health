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
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.model.PgContext.DEFAULT_SCHEMA_NAME;

public abstract class AbstractDbStatement implements DbStatement {

    private static final AtomicReference<String> SCHEMA_NAME_HOLDER = new AtomicReference<>(DEFAULT_SCHEMA_NAME);

    @Nonnull
    protected static String getSchemaName() {
        return SCHEMA_NAME_HOLDER.get();
    }

    public static String setSchemaName(@Nonnull final String schemaName) {
        return SCHEMA_NAME_HOLDER.getAndSet(schemaName);
    }

    protected void throwExceptionIfTableDoesNotExist(
        @Nonnull final Statement statement,
        @Nonnull final String tableName,
        @Nonnull final String schemaName
    ) throws SQLException {
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

    protected abstract List<String> getSqlToExecute();

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        final String schemaName = getSchemaName();
        for (final String sql : getSqlToExecute()) {
            statement.execute(sql.replace("{schemaName}", schemaName));
        }
        postExecute(statement, schemaName);
    }

    protected void postExecute(@Nonnull final Statement statement, @Nonnull final String schemaName) throws SQLException {
        //This method is intended to be overridden by subclasses to perform any post-execution logic.
    }
}
