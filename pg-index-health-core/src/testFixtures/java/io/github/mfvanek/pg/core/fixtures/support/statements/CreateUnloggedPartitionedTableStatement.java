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

public class CreateUnloggedPartitionedTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create unlogged table {schemaName}.unlogged_partitioned_table(
                    id      bigint not null,
                    created date   not null
                ) partition by range (created);""",
            """
                create unlogged table {schemaName}.unlogged_partitioned_table_p1
                    partition of {schemaName}.unlogged_partitioned_table
                    for values from ('2025-01-01') to ('2026-01-01');"""
        );
    }
}
