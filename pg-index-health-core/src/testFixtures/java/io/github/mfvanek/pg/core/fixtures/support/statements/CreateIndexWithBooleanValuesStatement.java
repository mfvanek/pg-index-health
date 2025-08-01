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

public class CreateIndexWithBooleanValuesStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create index if not exists i_accounts_deleted
                    on {schemaName}.accounts (deleted)""",
            """
                create unique index if not exists i_accounts_account_number_deleted
                    on {schemaName}.accounts (account_number, deleted)"""
        );
    }
}
