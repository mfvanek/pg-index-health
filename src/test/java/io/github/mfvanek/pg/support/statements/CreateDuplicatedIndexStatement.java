/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
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

public class CreateDuplicatedIndexStatement extends AbstractDbStatement {

    public CreateDuplicatedIndexStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("create index if not exists i_accounts_account_number " +
                "on %s.accounts (account_number)", schemaName));
        statement.execute(String.format("create index if not exists i_accounts_account_number_not_deleted " +
                "on %s.accounts (account_number) where not deleted", schemaName));
        statement.execute(String.format("create index if not exists i_accounts_number_balance_not_deleted " +
                "on %s.accounts (account_number, account_balance) where not deleted", schemaName));
        statement.execute(String.format("create index if not exists i_clients_last_first " +
                "on %s.clients (last_name, first_name)", schemaName));
        statement.execute(String.format("create index if not exists i_clients_last_name " +
                "on %s.clients (last_name)", schemaName));
        statement.execute(String.format("create index if not exists i_accounts_id_account_number_not_deleted " +
                "on %s.accounts (id, account_number) where not deleted", schemaName));
    }
}
