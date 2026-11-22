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
 * Represents a database statement that creates indexes on boolean columns and an index on a nullable column.
 * Includes a multicolumn index with an expression and an index with a boolean column only in the {@code INCLUDE} clause.
 * Also creates partial indexes on the nullable column: one with a predicate on this column
 * and one with a predicate on another column whose text contains the name of the nullable column.
 */
public class CreateIndexesWithBooleanAndNullValuesStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateIndexesWithBooleanAndNullValuesStatement}.
     */
    public CreateIndexesWithBooleanAndNullValuesStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create index if not exists i_accounts_deleted
                    on {schemaName}.accounts (deleted)""",
            """
                create unique index if not exists i_accounts_account_number_deleted
                    on {schemaName}.accounts (account_number, deleted)""",
            """
                create index if not exists i_accounts_balance_lower_number_deleted
                    on {schemaName}.accounts (account_balance, lower(account_number), deleted)""",
            """
                create index if not exists i_accounts_balance_include_deleted
                    on {schemaName}.accounts (account_balance) include (deleted)""",
            "create index if not exists i_clients_middle_name on {schemaName}.clients (middle_name)",
            "create index if not exists i_clients_middle_name_not_null on {schemaName}.clients (middle_name) where middle_name is not null",
            "create index if not exists i_clients_middle_name_known_last_name on {schemaName}.clients (middle_name) where last_name <> 'no_middle_name'"
        );
    }
}
