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

import java.util.List;

/**
 * Represents a database statement that creates indexes on {@code accounts} that do not cover the foreign key on {@code client_id}:
 * an index with the foreign key column not in the first position, partial indexes with unsuitable predicates and a BRIN index.
 */
public class CreateNotCoveringIndexesForForeignKeyStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateNotCoveringIndexesForForeignKeyStatement}.
     */
    public CreateNotCoveringIndexesForForeignKeyStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create index if not exists i_accounts_account_number_client_id on {schemaName}.accounts (account_number, client_id)",
            "create index if not exists i_accounts_client_id_not_deleted on {schemaName}.accounts (client_id) where not deleted",
            "create index if not exists i_accounts_client_id_not_null_not_deleted on {schemaName}.accounts (client_id) where client_id is not null and not deleted",
            "create index if not exists i_accounts_client_id_is_null on {schemaName}.accounts (client_id) where client_id is null",
            "create index if not exists i_accounts_client_id_brin on {schemaName}.accounts using brin (client_id)"
        );
    }
}
