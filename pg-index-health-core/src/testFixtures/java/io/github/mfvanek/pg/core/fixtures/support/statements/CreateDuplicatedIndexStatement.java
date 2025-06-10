/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

public class CreateDuplicatedIndexStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create index if not exists i_accounts_account_number
                    on {schemaName}.accounts (account_number)""",
            """
                create index if not exists i_accounts_account_number_not_deleted
                    on {schemaName}.accounts (account_number) where not deleted""",
            """
                create index if not exists i_accounts_number_balance_not_deleted
                    on {schemaName}.accounts (account_number, account_balance) where not deleted""",
            """
                create index if not exists i_clients_last_first
                    on {schemaName}.clients (last_name, first_name)""",
            """
                create index if not exists i_clients_last_name
                    on {schemaName}.clients (last_name)""",
            """
                create index if not exists i_accounts_id_account_number_not_deleted
                    on {schemaName}.accounts (id, account_number) where not deleted"""
        );
    }
}
