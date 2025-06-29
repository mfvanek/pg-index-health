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

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CreateTableWithColumnOfBigSerialTypeStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of("""
            create table if not exists {schemaName}.bad_accounts (
                id bigserial not null primary key,
                name varchar(255) not null,
                /* not null constraint will be added for all serial columns */
                real_client_id bigserial,
                real_account_id bigserial
            )""");
    }

    @Override
    public void postExecute(final Statement statement, final String schemaName) throws SQLException {
        throwExceptionIfTableDoesNotExist(statement, "bad_accounts", schemaName);
    }
}
