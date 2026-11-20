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
 * Represents a database statement that creates tables with cyclic foreign keys and tables with self-referenced foreign keys.
 * <p>
 * Table names are chosen so that their order does not depend on the collation of the database.
 * </p>
 */
public class CreateTablesWithCyclicAndSelfReferencedForeignKeysStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateTablesWithCyclicAndSelfReferencedForeignKeysStatement}.
     */
    public CreateTablesWithCyclicAndSelfReferencedForeignKeysStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            // Two tables reference each other: a multicolumn foreign key and a deferrable foreign key on a nullable column
            """
                create table if not exists {schemaName}.cfk_customers (
                    id bigint primary key,
                    region_id bigint not null,
                    last_order_id bigint,
                    constraint cfk_customers_id_region_uq unique (id, region_id)
                );""",
            """
                create table if not exists {schemaName}.cfk_orders (
                    id bigint primary key,
                    region_id bigint not null,
                    customer_id bigint not null,
                    constraint cfk_orders_customer_fk foreign key (customer_id, region_id) references {schemaName}.cfk_customers (id, region_id)
                );""",
            """
                alter table if exists {schemaName}.cfk_customers
                    add constraint cfk_customers_last_order_fk foreign key (last_order_id) references {schemaName}.cfk_orders (id) deferrable initially deferred;""",
            // Two parallel foreign keys between the same tables produce two separate cycles
            """
                create table if not exists {schemaName}.cfk_left (
                    id bigint primary key,
                    first_right_id bigint not null,
                    second_right_id bigint
                );""",
            """
                create table if not exists {schemaName}.cfk_right (
                    id bigint primary key,
                    left_id bigint not null,
                    constraint cfk_right_left_fk foreign key (left_id) references {schemaName}.cfk_left (id)
                );""",
            """
                alter table if exists {schemaName}.cfk_left
                    add constraint cfk_left_first_right_fk foreign key (first_right_id) references {schemaName}.cfk_right (id);""",
            """
                alter table if exists {schemaName}.cfk_left
                    add constraint cfk_left_second_right_fk foreign key (second_right_id) references {schemaName}.cfk_right (id);""",
            // A cycle of three tables. Table cfk_node_d references the cycle but is not a part of it. The self-referenced foreign key is ignored
            """
                create table if not exists {schemaName}.cfk_node_a (
                    id bigint primary key,
                    b_id bigint not null
                );""",
            """
                create table if not exists {schemaName}.cfk_node_b (
                    id bigint primary key,
                    c_id bigint not null,
                    parent_id bigint,
                    constraint cfk_node_b_parent_fk foreign key (parent_id) references {schemaName}.cfk_node_b (id)
                );""",
            """
                create table if not exists {schemaName}.cfk_node_c (
                    id bigint primary key,
                    a_id bigint not null,
                    constraint cfk_node_c_a_fk foreign key (a_id) references {schemaName}.cfk_node_a (id)
                );""",
            """
                create table if not exists {schemaName}.cfk_node_d (
                    id bigint primary key,
                    a_id bigint not null,
                    constraint cfk_node_d_a_fk foreign key (a_id) references {schemaName}.cfk_node_a (id)
                );""",
            """
                alter table if exists {schemaName}.cfk_node_a
                    add constraint cfk_node_a_b_fk foreign key (b_id) references {schemaName}.cfk_node_b (id);""",
            """
                alter table if exists {schemaName}.cfk_node_b
                    add constraint cfk_node_b_c_fk foreign key (c_id) references {schemaName}.cfk_node_c (id);""",
            // Overlapping cycles a -> c -> a and a -> c -> b -> a share the foreign key cfk_ring_a_c_fk
            """
                create table if not exists {schemaName}.cfk_ring_a (
                    id bigint primary key,
                    c_id bigint not null
                );""",
            """
                create table if not exists {schemaName}.cfk_ring_b (
                    id bigint primary key,
                    a_id bigint not null,
                    constraint cfk_ring_b_a_fk foreign key (a_id) references {schemaName}.cfk_ring_a (id)
                );""",
            """
                create table if not exists {schemaName}.cfk_ring_c (
                    id bigint primary key,
                    a_id bigint not null,
                    b_id bigint not null,
                    constraint cfk_ring_c_a_fk foreign key (a_id) references {schemaName}.cfk_ring_a (id),
                    constraint cfk_ring_c_b_fk foreign key (b_id) references {schemaName}.cfk_ring_b (id)
                );""",
            """
                alter table if exists {schemaName}.cfk_ring_a
                    add constraint cfk_ring_a_c_fk foreign key (c_id) references {schemaName}.cfk_ring_c (id);""",
            // Quoted identifiers
            """
                create table if not exists {schemaName}."Cfk-A-First" (
                    id bigint primary key,
                    "Second-Id" bigint not null
                );""",
            """
                create table if not exists {schemaName}."Cfk-A-Second" (
                    id bigint primary key,
                    "First-Id" bigint,
                    constraint "Cfk-A-Second-Fk" foreign key ("First-Id") references {schemaName}."Cfk-A-First" (id)
                );""",
            """
                alter table if exists {schemaName}."Cfk-A-First"
                    add constraint "Cfk-A-First-Fk" foreign key ("Second-Id") references {schemaName}."Cfk-A-Second" (id);""",
            // Self-referenced foreign keys: without ON DELETE rule, with ON DELETE RESTRICT and with ON DELETE CASCADE
            """
                create table if not exists {schemaName}.bad_self_ref_table (
                    id bigint generated always as identity primary key,
                    parent_id bigint,
                    name text not null,
                    constraint bad_self_ref_table_parent_id_fk
                        foreign key (parent_id) references {schemaName}.bad_self_ref_table (id)
                );""",
            """
                create table if not exists {schemaName}.bad_self_ref_restrict_table (
                    id bigint generated always as identity primary key,
                    parent_id bigint,
                    name text not null,
                    constraint bad_self_ref_restrict_table_parent_id_fk
                        foreign key (parent_id) references {schemaName}.bad_self_ref_restrict_table (id)
                        on delete restrict
                );""",
            """
                create table if not exists {schemaName}.good_self_ref_table (
                    id bigint generated always as identity primary key,
                    parent_id bigint,
                    name text not null,
                    constraint good_self_ref_table_parent_id_fk
                        foreign key (parent_id) references {schemaName}.good_self_ref_table (id)
                        on delete cascade
                );"""
        );
    }
}
