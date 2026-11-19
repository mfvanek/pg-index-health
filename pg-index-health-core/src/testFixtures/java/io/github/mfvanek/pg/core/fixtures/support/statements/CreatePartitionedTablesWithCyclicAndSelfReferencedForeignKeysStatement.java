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
 * Represents a database statement that creates partitioned tables with cyclic foreign keys
 * and a partitioned table with a self-referencing foreign key.
 */
public class CreatePartitionedTablesWithCyclicAndSelfReferencedForeignKeysStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreatePartitionedTablesWithCyclicAndSelfReferencedForeignKeysStatement}.
     */
    public CreatePartitionedTablesWithCyclicAndSelfReferencedForeignKeysStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            // A partitioned table and a regular table reference each other
            """
                create table if not exists {schemaName}.cfk_part_p (
                    id bigint primary key,
                    q_id bigint
                ) partition by range (id);""",
            "create table if not exists {schemaName}.cfk_part_p_1 partition of {schemaName}.cfk_part_p for values from (1) to (100);",
            "create table if not exists {schemaName}.cfk_part_p_2 partition of {schemaName}.cfk_part_p for values from (100) to (200);",
            """
                create table if not exists {schemaName}.cfk_part_q (
                    id bigint primary key,
                    p_id bigint not null,
                    constraint cfk_part_q_p_fk foreign key (p_id) references {schemaName}.cfk_part_p (id)
                );""",
            """
                alter table if exists {schemaName}.cfk_part_p
                    add constraint cfk_part_p_q_fk foreign key (q_id) references {schemaName}.cfk_part_q (id);""",
            // Two partitioned tables reference each other
            """
                create table if not exists {schemaName}.cfk_part_r (
                    id bigint primary key,
                    s_id bigint not null
                ) partition by range (id);""",
            "create table if not exists {schemaName}.cfk_part_r_1 partition of {schemaName}.cfk_part_r for values from (1) to (100);",
            "create table if not exists {schemaName}.cfk_part_r_2 partition of {schemaName}.cfk_part_r for values from (100) to (200);",
            """
                create table if not exists {schemaName}.cfk_part_s (
                    id bigint primary key,
                    r_id bigint not null,
                    constraint cfk_part_s_r_fk foreign key (r_id) references {schemaName}.cfk_part_r (id)
                ) partition by hash (id);""",
            "create table if not exists {schemaName}.cfk_part_s_1 partition of {schemaName}.cfk_part_s for values with (modulus 2, remainder 0);",
            "create table if not exists {schemaName}.cfk_part_s_2 partition of {schemaName}.cfk_part_s for values with (modulus 2, remainder 1);",
            """
                alter table if exists {schemaName}.cfk_part_r
                    add constraint cfk_part_r_s_fk foreign key (s_id) references {schemaName}.cfk_part_s (id);""",
            // Foreign keys declared directly on a partition or referencing a partition directly are ignored
            """
                create table if not exists {schemaName}.cfk_part_t (
                    id bigint primary key,
                    u_id bigint not null
                ) partition by range (id);""",
            "create table if not exists {schemaName}.cfk_part_t_1 partition of {schemaName}.cfk_part_t for values from (1) to (100);",
            "create table if not exists {schemaName}.cfk_part_t_2 partition of {schemaName}.cfk_part_t for values from (100) to (200);",
            """
                create table if not exists {schemaName}.cfk_part_u (
                    id bigint primary key,
                    t_id bigint not null,
                    constraint cfk_part_u_t_1_fk foreign key (t_id) references {schemaName}.cfk_part_t_1 (id)
                );""",
            """
                alter table if exists {schemaName}.cfk_part_t_1
                    add constraint cfk_part_t_1_u_fk foreign key (u_id) references {schemaName}.cfk_part_u (id);""",
            // Partitioned table with a self-referencing foreign key
            """
                create table if not exists {schemaName}.bad_self_ref_partitioned (
                    id bigint not null,
                    parent_id bigint,
                    name text not null,
                    primary key (id),
                    constraint bad_self_ref_partitioned_parent_id_fk
                        foreign key (parent_id) references {schemaName}.bad_self_ref_partitioned (id)
                ) partition by hash (id);""",
            """
                create table if not exists {schemaName}.bad_self_ref_partitioned_0
                    partition of {schemaName}.bad_self_ref_partitioned for values with (modulus 2, remainder 0);""",
            """
                create table if not exists {schemaName}.bad_self_ref_partitioned_1
                    partition of {schemaName}.bad_self_ref_partitioned for values with (modulus 2, remainder 1);"""
        );
    }
}
