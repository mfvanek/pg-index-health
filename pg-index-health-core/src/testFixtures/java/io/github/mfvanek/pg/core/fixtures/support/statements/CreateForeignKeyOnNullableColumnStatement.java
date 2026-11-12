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
 * Represents a database statement that creates a foreign key referencing a nullable column.
 */
public class CreateForeignKeyOnNullableColumnStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateForeignKeyOnNullableColumnStatement}.
     */
    public CreateForeignKeyOnNullableColumnStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                alter table if exists {schemaName}.bad_clients
                    add constraint c_bad_clients_fk_real_client_id
                    foreign key (real_client_id) references {schemaName}.clients (id);""",
            """
                alter table if exists {schemaName}.bad_clients
                    add constraint c_bad_clients_fk_email_phone
                    foreign key (email, phone) references {schemaName}.clients (email, phone);"""
        );
    }
}
