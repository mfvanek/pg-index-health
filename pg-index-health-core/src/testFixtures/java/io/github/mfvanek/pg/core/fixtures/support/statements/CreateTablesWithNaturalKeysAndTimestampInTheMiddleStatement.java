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
 * Represents a database statement that creates tables with natural primary keys and a table with timestamp columns in the middle of indexes.
 */
public class CreateTablesWithNaturalKeysAndTimestampInTheMiddleStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateTablesWithNaturalKeysAndTimestampInTheMiddleStatement}.
     */
    public CreateTablesWithNaturalKeysAndTimestampInTheMiddleStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.good (
                    id int not null primary key);""",
            """
                create table if not exists {schemaName}."times-of-creation" (
                    description text not null,
                    "time-of-creation" timestamptz not null primary key);""",
            """
                create table if not exists {schemaName}.t2_composite (
                    issue_date timestamptz not null,
                    passport_series text not null,
                    passport_number text not null,
                    primary key (passport_series, passport_number)
                );""",
            """
                create table if not exists {schemaName}.t3_composite (
                    app_id uuid not null,
                    column_in_the_middle text,
                    app_number text not null,
                    primary key (app_id, app_number)
                );""",
            """
                create table if not exists {schemaName}."t-multi" (
                    id int primary key,
                    ts timestamp,
                    created_at timetz,
                    name text
                );""",
            "create index idx_multi_mid on {schemaName}.\"t-multi\" (id, ts, name);",
            "create index idx_multi_end on {schemaName}.\"t-multi\" (id, name, ts);",
            "create index idx_multi_none on {schemaName}.\"t-multi\" (id, name);",
            "create index idx_multi_expr_mid on {schemaName}.\"t-multi\" (id, date_trunc('day', ts), name);",
            "create index idx_multi_expr_first on {schemaName}.\"t-multi\" (date_trunc('day', ts), id, name);",
            "create unique index idx_unique_ts on {schemaName}.\"t-multi\" (id, ts, id);"
        );
    }
}
