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

import java.util.List;
import javax.annotation.Nonnull;

public class CreateIndexesOnArrayColumn extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute(@Nonnull final String schemaName) {
        return List.of(
            String.format("alter table %s.accounts add column if not exists roles text[]", schemaName),
            String.format("create index if not exists accounts_roles_btree_idx on %s.accounts(roles) where roles is not null", schemaName),
            String.format("create index if not exists accounts_account_number_roles_btree_idx on %s.accounts(account_number, roles)", schemaName),
            String.format("create index if not exists accounts_account_number_including_roles_idx on %s.accounts(account_number) include (roles)", schemaName),
            String.format("create index if not exists accounts_roles_gin_idx on %s.accounts using gin(roles) where roles is not null", schemaName)
        );
    }
}
