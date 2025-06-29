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

import java.util.List;

public class CreateIndexesOnArrayColumnStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "alter table {schemaName}.accounts add column if not exists roles text[]",
            "create index if not exists accounts_roles_btree_idx on {schemaName}.accounts(roles) where roles is not null",
            "create index if not exists accounts_account_number_roles_btree_idx on {schemaName}.accounts(account_number, roles)",
            "create index if not exists accounts_account_number_including_roles_idx on {schemaName}.accounts(account_number) include (roles)",
            "create index if not exists accounts_roles_gin_idx on {schemaName}.accounts using gin(roles) where roles is not null"
        );
    }
}
