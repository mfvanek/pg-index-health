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
 * Represents a database statement that creates indexes on the same columns that are not duplicates of each other
 * because they differ in access method, operator class, collation or sort order.
 * Also creates an index starting with another expression that does not intersect with the expression indexes.
 * <p>
 * Requires the custom collation {@code C.UTF-8} (see {@link CreateCustomCollationStatement}).
 */
public class CreateNotDuplicatedIndexesStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateNotDuplicatedIndexesStatement}.
     */
    public CreateNotDuplicatedIndexesStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create index if not exists i_accounts_account_number_hash on {schemaName}.accounts using hash(account_number)",
            "create index if not exists i_accounts_account_number_collate_c on {schemaName}.accounts (account_number collate {schemaName}.\"C.UTF-8\")",
            "create index if not exists i_accounts_account_number_desc on {schemaName}.accounts (account_number desc)",
            "create index if not exists i_clients_last_first on {schemaName}.clients (last_name, first_name)",
            "create index if not exists i_clients_last_name_hash on {schemaName}.clients using hash(last_name)",
            "create index if not exists i_clients_lower_last_name on {schemaName}.clients using btree(lower(last_name))",
            "create index if not exists i_clients_lower_last_name_ops on {schemaName}.clients using btree(lower(last_name) text_pattern_ops)",
            "create index if not exists i_clients_upper_first_name_last_name on {schemaName}.clients (upper(first_name), last_name)"
        );
    }
}
