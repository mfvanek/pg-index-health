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

public class CreatePartitionedTableWithIncrementingColumnsStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.events (
                    id         bigint not null,
                    event_date date   not null,
                    tag1       text,
                    tag2       text
                ) partition by range (event_date);""",
            """
                create table if not exists {schemaName}.events_2024
                    partition of {schemaName}.events
                        for values from ('2024-01-01') to ('2025-01-01');"""
        );
    }
}
