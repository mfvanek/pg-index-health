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

public class CreateDuplicatedHashIndexStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create index if not exists i_accounts_account_number on {schemaName}.accounts using hash(account_number)",
            "create index if not exists i_clients_last_first on {schemaName}.clients (last_name, first_name)",
            "create index if not exists i_clients_last_name on {schemaName}.clients using hash(last_name)"
        );
    }
}
