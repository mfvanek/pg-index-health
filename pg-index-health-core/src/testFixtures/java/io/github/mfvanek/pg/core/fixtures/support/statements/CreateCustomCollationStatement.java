/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.core.fixtures.support.SchemaNameHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

public class CreateCustomCollationStatement extends AbstractDbStatement {

    private static final String ICU_COLLATION = "en-US-x-icu";

    @Override
    protected List<String> getSqlToExecute() {
        return List.of();
    }

    @Override
    public void execute(final Statement statement) throws SQLException {
        final String customCollation = "C.UTF-8";
        if (isCollationExist(statement, customCollation)) {
            return;
        }
        createCustomCollation(statement, customCollation);
    }

    private boolean isCollationExist(final Statement statement, final String collation) {
        try (ResultSet rs = statement.executeQuery(String.format(Locale.ROOT,
            "select exists(select 1 from pg_catalog.pg_collation as pgc where pgc.collname = '%s'::text)", collation))) {
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }

    private void createCustomCollation(final Statement statement,
                                       final String customCollation) throws SQLException {
        if (!isCollationExist(statement, ICU_COLLATION)) {
            throw new IllegalStateException(String.format(Locale.ROOT, "System collation '%s' not found", ICU_COLLATION));
        }
        statement.execute(String.format(Locale.ROOT,
            "create collation %s.\"%s\" from \"%s\";", SchemaNameHolder.getSchemaName(), customCollation, ICU_COLLATION));
    }
}
