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
 * Represents a database statement that creates partitioned tables with varchar, char, money, timestamp,
 * blob, enum, JSON, serial and incrementing columns, and a partitioned table with very long names.
 */
public class CreatePartitionedTableWithVarcharStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreatePartitionedTableWithVarcharStatement}.
     */
    public CreatePartitionedTableWithVarcharStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.tp(
                    creation_date timestamp not null,
                    ref_type varchar(36) not null,
                    entity_id varchar(36) not null,
                    account_balance money not null default 0,
                    gender char not null,
                    home_address char(2000),
                    contact_person character(500),
                    nickname bpchar(200),
                    safe_word bpchar,
                    primary key (creation_date, ref_type, entity_id)
                ) partition by range (creation_date);""",
            """
                create index if not exists i_tp_creation_date_entity_id_ref_type
                    on {schemaName}.tp (creation_date, entity_id, ref_type);""",
            """
                create table if not exists {schemaName}.tp_default
                    partition of {schemaName}.tp default;""",
            """
                create table if not exists {schemaName}.tp_good (
                    creation_date timestamp not null,
                    entity_id uuid not null,
                    primary key (creation_date, entity_id)
                ) partition by range (creation_date);""",
            """
                create table if not exists {schemaName}."tp_good-default"
                    partition of {schemaName}.tp_good default;""",
            """
                create table if not exists {schemaName}.attachment (
                    id bigserial not null,
                    created_at date not null,
                    file_data oid
                ) partition by range (created_at);""",
            """
                create table if not exists {schemaName}.attachment_2024
                    partition of {schemaName}.attachment for values from ('2024-01-01') to ('2025-01-01');""",
            "create type {schemaName}.delivery_status as enum ('new', 'delivered');",
            """
                create table if not exists {schemaName}.deliveries (
                    id bigint not null,
                    created_at timestamptz not null,
                    status {schemaName}.delivery_status not null,
                    primary key (id, created_at)
                ) partition by range (created_at);""",
            "create table if not exists {schemaName}.deliveries_default partition of {schemaName}.deliveries default;",
            """
                create table if not exists {schemaName}.parent(
                    ref_type varchar(32),
                    ref_value varchar(64),
                    creation_date timestamp with time zone not null,
                    entity_id varchar(64) not null,
                    real_client_id bigserial,
                    raw_data json
                ) partition by range (creation_date);""",
            """
                create table if not exists {schemaName}.partition_default
                    partition of {schemaName}.parent default;""",
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
                        for values from ('2024-01-01') to ('2025-01-01');""",
            """
                create table if not exists {schemaName}.entity_long_1234567890_1234567890_1234567890_1234567890_1234567(
                    ref_type varchar(32),
                    ref_value varchar(64),
                    entity_id bigserial primary key
                ) partition by range (entity_id);""",
            """
                create index if not exists idx_entity_long_1234567890_1234567890_1234567890_1234567890_123
                    on {schemaName}.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 (ref_type, ref_value);""",
            """
                create table if not exists {schemaName}.entity_default_long_1234567890_1234567890_1234567890_1234567890
                    partition of {schemaName}.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 default;"""
        );
    }
}
