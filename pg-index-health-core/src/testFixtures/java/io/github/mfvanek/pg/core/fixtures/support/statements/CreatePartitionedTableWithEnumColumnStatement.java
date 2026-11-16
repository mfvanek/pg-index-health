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
 * Represents a database statement that creates a partitioned table with an enum column.
 */
public class CreatePartitionedTableWithEnumColumnStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreatePartitionedTableWithEnumColumnStatement}.
     */
    public CreatePartitionedTableWithEnumColumnStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create type {schemaName}.delivery_status as enum ('new', 'delivered');",
            """
                create table if not exists {schemaName}.deliveries (
                    id bigint not null,
                    created_at timestamptz not null,
                    status {schemaName}.delivery_status not null,
                    primary key (id, created_at)
                ) partition by range (created_at);""",
            "create table if not exists {schemaName}.deliveries_default partition of {schemaName}.deliveries default;"
        );
    }
}
