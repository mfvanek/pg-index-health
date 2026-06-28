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

public class CreatePartitionedTableWithInconsistentTypesStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        // Two partitioned (parent) tables with inconsistent types for the same column names:
        // "id" is uuid here and bigint there (and bigint in the base tables);
        // "created_at" is timestamptz here and timestamp there.
        // Child partitions repeat the same columns but must be ignored by the check.
        return List.of(
            """
                create table if not exists {schemaName}.pt_one (
                    id uuid not null,
                    created_at timestamptz not null,
                    primary key (id, created_at)
                ) partition by range (created_at);""",
            """
                create table if not exists {schemaName}.pt_one_default
                    partition of {schemaName}.pt_one default;""",
            """
                create table if not exists {schemaName}.pt_two (
                    id bigint not null,
                    created_at timestamp not null,
                    primary key (id, created_at)
                ) partition by range (created_at);""",
            """
                create table if not exists {schemaName}.pt_two_default
                    partition of {schemaName}.pt_two default;"""
        );
    }
}
