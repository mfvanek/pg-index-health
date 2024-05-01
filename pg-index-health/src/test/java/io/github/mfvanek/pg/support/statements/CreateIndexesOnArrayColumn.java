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

public class CreateIndexesOnArrayColumn extends AbstractDbStatement {

    public CreateIndexesOnArrayColumn(final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("alter table %s.accounts add column if not exists roles text[]", schemaName));
        statement.execute(String.format("create index if not exists accounts_roles_btree_idx on %s.accounts(roles) where roles is not null", schemaName));
        statement.execute(String.format("create index if not exists accounts_account_number_roles_btree_idx on %s.accounts(account_number, roles)", schemaName));
        statement.execute(String.format("create index if not exists accounts_account_number_including_roles_idx on %s.accounts(account_number) include (roles)", schemaName));
        statement.execute(String.format("create index if not exists accounts_roles_gin_idx on %s.accounts using gin(roles) where roles is not null", schemaName));
    }
}
