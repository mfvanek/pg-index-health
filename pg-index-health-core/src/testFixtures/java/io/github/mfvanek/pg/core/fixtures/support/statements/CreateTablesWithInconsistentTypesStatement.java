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
 * Represents a database statement that creates tables with inconsistent column data types across tables.
 */
public class CreateTablesWithInconsistentTypesStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateTablesWithInconsistentTypesStatement}.
     */
    public CreateTablesWithInconsistentTypesStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        // The base "clients" and "accounts" tables already declare "id" as bigint,
        // so adding "id" of int and uuid makes the type for the "id" name inconsistent across the schema.
        // The "created_at" name is inconsistent on its own: timestamp here, timestamptz in the other table.
        return List.of(
            """
                create table if not exists {schemaName}."t-int-id" (
                    id int not null primary key,
                    created_at timestamp not null
                );""",
            """
                create table if not exists {schemaName}.t_uuid_id (
                    id uuid not null primary key,
                    created_at timestamptz not null
                );"""
        );
    }
}
