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

public class AddCommentOnColumnsStatement extends AbstractDbStatement {

    public AddCommentOnColumnsStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("comment on column %1$s.clients.id is 'Unique record ID';" +
            "comment on column %1$s.clients.last_name is 'Customer''s last name';" +
            "comment on column %1$s.clients.first_name is 'Customer''s given name';" +
            "comment on column %1$s.clients.middle_name is 'Patronymic of the customer';" +
            "comment on column %1$s.clients.info is 'Raw client data';" +
            "comment on column %1$s.accounts.id is 'Unique record ID';" +
            "comment on column %1$s.accounts.client_id is 'Customer record ID';" +
            "comment on column %1$s.accounts.account_number is 'Customer''s account number';" +
            "comment on column %1$s.accounts.account_balance is 'The balance on the customer''s account';" +
            "comment on column %1$s.accounts.deleted is 'Indicates that the account has been deleted';", schemaName));
    }
}
