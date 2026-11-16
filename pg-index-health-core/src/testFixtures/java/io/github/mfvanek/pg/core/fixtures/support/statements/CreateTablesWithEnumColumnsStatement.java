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
 * Represents a database statement that creates tables with enum columns.
 */
public class CreateTablesWithEnumColumnsStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateTablesWithEnumColumnsStatement}.
     */
    public CreateTablesWithEnumColumnsStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create type {schemaName}.order_status as enum ('new', 'paid', 'shipped');",
            """
                create table if not exists {schemaName}.orders (
                    id bigint generated always as identity primary key,
                    status {schemaName}.order_status not null,
                    previous_status {schemaName}.order_status,
                    status_history {schemaName}.order_status[],
                    status_matrix {schemaName}.order_status[][] not null,
                    status_text text not null check (status_text in ('new', 'paid', 'shipped')),
                    tags text[],
                    numbers integer[],
                    full_name name,
                    location point,
                    legacy_status {schemaName}.order_status
                );""",
            "alter table if exists {schemaName}.orders drop column legacy_status;",
            "create index if not exists i_orders_status on {schemaName}.orders (status);",
            "create or replace view {schemaName}.v_orders as select id, status, status_history from {schemaName}.orders;",
            "create materialized view if not exists {schemaName}.mv_orders as select id, status, status_history from {schemaName}.orders;",
            """
                create table if not exists {schemaName}."Bad-Orders" (
                    id bigint primary key,
                    "Bad-Status" {schemaName}.order_status not null
                );"""
        );
    }
}
