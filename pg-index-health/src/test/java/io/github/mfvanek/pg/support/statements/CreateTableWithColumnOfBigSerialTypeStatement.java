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

import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

public class CreateTableWithColumnOfBigSerialTypeStatement extends AbstractDbStatement {

    public CreateTableWithColumnOfBigSerialTypeStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("create table if not exists %s.bad_accounts (" +
                "id bigserial not null primary key, " +
                "name varchar(255) not null," +
                // not null constraint will be added for all serial columns
                "real_client_id bigserial," +
                "real_account_id bigserial)", schemaName));
        throwExceptionIfTableDoesNotExist(statement, "bad_accounts");
    }
}
