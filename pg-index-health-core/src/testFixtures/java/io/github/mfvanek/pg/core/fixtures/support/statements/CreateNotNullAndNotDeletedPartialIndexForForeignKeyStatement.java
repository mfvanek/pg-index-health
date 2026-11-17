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
 * Represents a database statement that creates a partial index for a foreign key with a predicate {@code fk_column is not null and not deleted}.
 */
public class CreateNotNullAndNotDeletedPartialIndexForForeignKeyStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateNotNullAndNotDeletedPartialIndexForForeignKeyStatement}.
     */
    public CreateNotNullAndNotDeletedPartialIndexForForeignKeyStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of("""
            create index if not exists
                i_accounts_client_id_not_null_not_deleted on {schemaName}.accounts (client_id) where client_id is not null and not deleted"""
        );
    }
}
