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

public class CreatePartitionedTableWithBlobTypeColumnStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.attachment (
                    id bigserial not null,
                    created_at date not null,
                    file_data oid
                ) partition by range (created_at);""",
            """
                create table if not exists {schemaName}.attachment_2024
                    partition of {schemaName}.attachment for values from ('2024-01-01') to ('2025-01-01');"""
        );
    }
}
