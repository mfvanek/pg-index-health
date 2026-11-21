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
 * Represents a database statement that creates indexes covering all foreign keys:
 * an ordinary index with the foreign key column in the first position, partial indexes
 * with predicates consisting only of {@code fk_column is not null} conditions
 * and an index with columns of a composite foreign key in a different order.
 * Also adds an exclusion constraint on {@code bad_clients}: its index is neither unique nor serves a foreign key.
 */
public class CreateCoveringIndexesForForeignKeysStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateCoveringIndexesForForeignKeysStatement}.
     */
    public CreateCoveringIndexesForForeignKeysStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create index if not exists i_accounts_client_id_not_null on {schemaName}.accounts (client_id) where client_id is not null",
            "create index if not exists i_bad_clients_real_client_id_name on {schemaName}.bad_clients (real_client_id, name)",
            "create index if not exists i_bad_clients_email_phone_not_null on {schemaName}.bad_clients (email, phone) where email is not null and phone is not null",
            "create index if not exists i_referencing_bad_table_not_null on {schemaName}.referencing_bad_table (rbt_id, rbt_value) where rbt_value is not null",
            "create index if not exists i_referencing_good_table_not_null on {schemaName}.referencing_good_table (rgt_value, rgt_id) where rgt_id is not null",
            "alter table if exists {schemaName}.bad_clients add constraint c_bad_clients_name_excl exclude using btree (name with =)"
        );
    }
}
