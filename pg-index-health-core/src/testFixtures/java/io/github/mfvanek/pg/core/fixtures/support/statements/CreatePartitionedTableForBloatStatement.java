/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

public class CreatePartitionedTableForBloatStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.orders_partitioned(
                    id         bigint not null generated always as identity,
                    user_id    bigint not null,
                    shop_id    bigint not null,
                    status     int not null,
                    created_at timestamptz not null default current_timestamp,
                    primary key (id, created_at)
                ) partition by range (created_at);""",
            """
                create table if not exists {schemaName}.orders_default
                    partition of {schemaName}.orders_partitioned default;""",
            """
                create table if not exists {schemaName}.order_item_partitioned(
                    id           bigint generated always as identity,
                    order_id     bigint not null,
                    created_at   timestamptz not null,
                    price        decimal(22, 2) not null default 0,
                    amount       int not null default 0,
                    sku          varchar(255) not null,
                    warehouse_id int,
                    primary key (id, created_at),
                    constraint fk_order_item_order_id foreign key (order_id, created_at)
                        references {schemaName}.orders_partitioned (id, created_at)
                ) partition by range (created_at);""",
            """
                create index if not exists idx_order_item_partitioned_order_id
                    on {schemaName}.order_item_partitioned (order_id);""",
            """
                create index if not exists idx_order_item_partitioned_warehouse_id_without_nulls
                    on {schemaName}.order_item_partitioned (warehouse_id) where warehouse_id is not null;""",
            """
                create table if not exists {schemaName}.order_item_default
                    partition of {schemaName}.order_item_partitioned default;""",
            """
                insert into {schemaName}.orders_partitioned (user_id, shop_id, status)
                select (ids.id % 10) + 1 as user_id,
                       (ids.id % 4) + 1 as shop_id,
                       1 as status
                from generate_series(1, 10000) ids (id);""",
            """
                insert into {schemaName}.order_item_partitioned (order_id, created_at, price, amount, sku)
                select id as order_id, created_at,
                       (random() + 1) * 1000.0 as price,
                       (random() * 10) + 1 as amount,
                       md5(random()::text) as sku
                from {schemaName}.orders_partitioned;""",
            """
                insert into {schemaName}.order_item_partitioned (order_id, created_at, price, amount, sku)
                select id as order_id, created_at,
                       (random() + 1) * 2000.0 as price,
                       (random() * 5) + 1 as amount,
                       md5((random() + 1)::text) as sku
                from {schemaName}.orders_partitioned
                where id % 2 = 0;""",
            """
                update {schemaName}.orders_partitioned
                set status = 2 /* paid order */
                where status = 1 /* new order */
                  and id in (select id
                             from {schemaName}.orders_partitioned
                             where id % 4 = 0
                             order by id
                             limit 10000);""",
            """
                update {schemaName}.order_item_partitioned
                set warehouse_id = case when order_id % 8 = 0 then 1 else 2 end
                where warehouse_id is null
                  and order_id in (select id
                                   from {schemaName}.orders_partitioned
                                   where status = 2
                                     and created_at >= current_timestamp - interval '1 day');"""
        );
    }
}
