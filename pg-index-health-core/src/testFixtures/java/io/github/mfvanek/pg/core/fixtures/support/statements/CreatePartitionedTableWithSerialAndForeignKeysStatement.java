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
 * Represents a database statement that creates a partitioned table with serial primary key and foreign keys,
 * duplicated, intersected, partial and array-column indexes, not valid constraints
 * and one more partitioned table with a foreign key covered by a partial index.
 */
public class CreatePartitionedTableWithSerialAndForeignKeysStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreatePartitionedTableWithSerialAndForeignKeysStatement}.
     */
    public CreatePartitionedTableWithSerialAndForeignKeysStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.t1(
                    ref_value varchar(64) not null,
                    ref_type bigserial not null references {schemaName}.dict(ref_type),
                    creation_date timestamp with time zone not null,
                    entity_id varchar(64) not null,
                    deleted boolean not null,
                    primary key (ref_value, ref_type, creation_date, entity_id)
                ) partition by range (creation_date);""",
            "create index if not exists idx_t1_deleted on {schemaName}.t1(deleted);",
            """
                create table if not exists {schemaName}.t1_default
                    partition of {schemaName}.t1 default;""",
            "create index if not exists idx_t1_deleted_duplicate on {schemaName}.t1(deleted);",
            "create index if not exists idx_t1_deleted_entity_id on {schemaName}.t1(deleted, entity_id);",
            """
                alter table if exists {schemaName}.t1
                add constraint t1_entity_id_not_validated_yet
                check (entity_id != '') not valid;""",
            """
                alter table if exists {schemaName}.t1_default
                add constraint t1_default_entity_id_not_validated_yet
                check (entity_id != '') not valid;""",
            "alter table if exists {schemaName}.t1 add column if not exists roles text[];",
            "create index if not exists t1_roles_btree_idx on {schemaName}.t1 (roles) where roles is not null;",
            """
                create index if not exists
                    idx_t1_ref_type_not_deleted on {schemaName}.t1 (ref_type) where not deleted;""",
            """
                create table if not exists {schemaName}.t3(
                    ref_type bigint references {schemaName}.dict(ref_type),
                    creation_date timestamp with time zone not null
                ) partition by range (creation_date);""",
            "create index if not exists idx_t3_ref_type_not_null on {schemaName}.t3 (ref_type) where ref_type is not null;",
            "create table if not exists {schemaName}.t3_default partition of {schemaName}.t3 default;",
            """
                create table if not exists {schemaName}.referencing_bad_table_partitioned(
                    rbt_id integer not null,
                    rbt_value text, /* nullable */
                    created_at timestamptz not null default current_timestamp,
                    constraint "referencing_bad_table_partitioned-fk" foreign key (rbt_id, rbt_value)
                        references {schemaName}.dict (ref_type, ref_value)
                ) partition by range (created_at);""",
            """
                create table if not exists {schemaName}.referencing_bad_table_default
                    partition of {schemaName}.referencing_bad_table_partitioned default;""",
            "insert into {schemaName}.referencing_bad_table_partitioned (rbt_id, rbt_value) values (20, '20');",
            "insert into {schemaName}.referencing_bad_table_partitioned (rbt_id, rbt_value) values (30, null);",
            """
                alter table if exists {schemaName}.t1
                    add constraint t1_ref_type_fk_duplicate
                    foreign key (ref_type) references {schemaName}.dict(ref_type);""",
            """
                alter table if exists {schemaName}.t1
                    add constraint t1_ref_type_ref_value_fk
                    foreign key (ref_type, ref_value) references {schemaName}.dict(ref_type, ref_value);"""
        );
    }
}
