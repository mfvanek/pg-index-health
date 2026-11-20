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
 * Represents a database statement that creates tables with serial columns:
 * a serial primary key with check constraints, a serial column with a unique constraint,
 * and a serial primary key that also references other tables.
 */
public class CreateTablesWithSerialColumnsStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateTablesWithSerialColumnsStatement}.
     */
    public CreateTablesWithSerialColumnsStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.another_table(
                    id bigserial primary key,
                    constraint not_reserved_id check (id > 1000),
                    constraint less_than_million check (id < 1000000)
                );""",
            """
                create table if not exists {schemaName}.one_more_table(
                    id bigserial,
                    constraint unique_id unique (id),
                    constraint not_reserved_id check (id > 1000),
                    constraint less_than_million check (id < 1000000)
                );""",
            """
                create table if not exists {schemaName}.test_table(
                    id bigserial,
                    num bigserial,
                    constraint test_table_pkey_id primary key (id),
                    constraint test_table_fkey_other_id foreign key (id) references {schemaName}.another_table (id),
                    constraint test_table_fkey_one_more_id foreign key (id) references {schemaName}.one_more_table (id)
                );"""
        );
    }
}
