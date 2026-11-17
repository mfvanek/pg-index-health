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
 * Represents a database statement that creates partial indexes with predicates {@code fk_column is not null} for all foreign keys.
 */
public class CreateNotNullPartialIndexesForForeignKeysStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateNotNullPartialIndexesForForeignKeysStatement}.
     */
    public CreateNotNullPartialIndexesForForeignKeysStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create index if not exists i_accounts_client_id_not_null on {schemaName}.accounts (client_id) where client_id is not null",
            "create index if not exists i_bad_clients_real_client_id_not_null on {schemaName}.bad_clients (real_client_id) where real_client_id is not null",
            "create index if not exists i_bad_clients_email_phone_not_null on {schemaName}.bad_clients (email, phone) where email is not null and phone is not null"
        );
    }
}
