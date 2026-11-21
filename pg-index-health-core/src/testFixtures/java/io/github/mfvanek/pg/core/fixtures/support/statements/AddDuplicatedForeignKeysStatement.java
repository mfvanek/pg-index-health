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
 * Represents a database statement that adds duplicated foreign keys between tables.
 * Also adds a foreign key on the same columns as {@code c_bad_clients_fk_email_phone}
 * that references other columns of the same table, so it is not a duplicate.
 * <p>
 * Requires the foreign keys on {@code bad_clients} (see {@link CreateForeignKeyOnNullableColumnStatement}).
 */
public class AddDuplicatedForeignKeysStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code AddDuplicatedForeignKeysStatement}.
     */
    public AddDuplicatedForeignKeysStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                alter table if exists {schemaName}.accounts
                    add constraint c_accounts_fk_client_id_duplicate
                    foreign key (client_id) references {schemaName}.clients (id);""",
            """
                alter table if exists {schemaName}.bad_clients
                    add constraint c_bad_clients_fk_email_phone_reversed
                    foreign key (email, phone) references {schemaName}.clients (phone, email);"""
        );
    }
}
