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

import io.github.mfvanek.pg.connection.PgSqlException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

public class CreateCustomCollationStatement extends AbstractDbStatement {

    private static final String ICU_COLLATION = "en-US-x-icu";

    public CreateCustomCollationStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        final String customCollation = "C.UTF-8";
        if (isCollationExist(statement, customCollation)) {
            return;
        }
        createCustomCollation(statement, customCollation);
    }

    private boolean isCollationExist(@Nonnull final Statement statement, @Nonnull final String collation) {
        final String sqlQuery = "select exists(select 1 from pg_catalog.pg_collation as pgc where pgc.collname = '%s'::text)";
        try (ResultSet rs = statement.executeQuery(String.format(sqlQuery, collation))) {
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }

    private void createCustomCollation(@Nonnull final Statement statement,
                                       @Nonnull final String customCollation) throws SQLException {
        if (!isCollationExist(statement, ICU_COLLATION)) {
            throw new IllegalStateException(String.format("System collation '%s' not found", ICU_COLLATION));
        }
        final String query = "create collation %s.\"%s\" from \"%s\";";
        statement.execute(String.format(query, schemaName, customCollation, ICU_COLLATION));
    }
}
