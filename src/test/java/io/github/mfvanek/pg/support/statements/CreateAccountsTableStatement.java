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

import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

public class CreateAccountsTableStatement extends AbstractDbStatement {

    public CreateAccountsTableStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("create sequence if not exists %s.accounts_seq", schemaName));
        statement.execute(String.format("create table if not exists %1$s.accounts (" +
                "id bigint not null primary key default nextval('%1$s.accounts_seq')," +
                "client_id bigint not null," +
                "account_number varchar(50) not null unique," +
                "account_balance numeric(22,2) not null default 0," +
                "deleted boolean not null default false)", schemaName));
    }
}
