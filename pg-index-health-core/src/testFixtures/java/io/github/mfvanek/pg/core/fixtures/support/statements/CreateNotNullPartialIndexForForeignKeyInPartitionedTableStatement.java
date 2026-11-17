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
 * Represents a database statement that creates a partitioned table with a foreign key covered by a partial index with a predicate {@code fk_column is not null}.
 */
public class CreateNotNullPartialIndexForForeignKeyInPartitionedTableStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateNotNullPartialIndexForForeignKeyInPartitionedTableStatement}.
     */
    public CreateNotNullPartialIndexForForeignKeyInPartitionedTableStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.t3(
                    ref_type bigint references {schemaName}.dict(ref_type),
                    creation_date timestamp with time zone not null
                ) partition by range (creation_date);""",
            "create index if not exists idx_t3_ref_type_not_null on {schemaName}.t3 (ref_type) where ref_type is not null;",
            "create table if not exists {schemaName}.t3_default partition of {schemaName}.t3 default;"
        );
    }
}
