# Check for index bloat in tables

## Why indexes become bloated

Frequent data updates can lead to index bloat for the same reason as
[table bloat](bloated_tables.md)

## Why index bloat should be monitored

If an index is bloated, performance decreases (more pages need to be read from disk).

## SQL query

- [bloated_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_indexes.sql)

## Check type

- **runtime** (requires accumulated statistics)

## Support for partitioned tables

Supports partitioned tables. The bloat percentage is calculated separately for each partition.

## How this check works

To execute the query, the user needs read permissions on the tables being checked.

### Working principle

An SQL query is executed against tables in the `pg_catalog` system schema.
They contain statistical information about core objects: tables, indexes, columns.

First, the query collects data about B-tree indexes in the specified schema.
Each index column is considered separately.
Indexes are linked to the corresponding table columns.
The collected parameters make it possible to estimate the number of pages that should be used by the index.
This is compared with the actual number of pages. Only indexes with available statistics are considered.
After that, the difference between the actual and estimated number of index pages is calculated, and based on it,
the index bloat percentage is computed.
If it exceeds the configured threshold (default is 10%), the index is considered bloated.
The results are sorted by table name and index name.

## Reproduction script

```sql
create schema if not exists demo;

-- For regular (non-partitioned) tables

create table if not exists demo.orders(
    id bigint primary key generated always as identity,
    user_id bigint not null,
    shop_id bigint not null,
    status int not null,
    created_at timestamptz not null default current_timestamp
);

create table if not exists demo.order_item(
    id bigint primary key generated always as identity,
    order_id bigint not null references demo.orders (id),
    price decimal(22, 2) not null default 0,
    amount int not null default 0,
    sku varchar(255) not null,
    warehouse_id int
);

create index if not exists idx_order_item_order_id
    on demo.order_item (order_id);

create index if not exists idx_order_item_warehouse_id_without_nulls
    on demo.order_item (warehouse_id) where warehouse_id is not null;

-- Filling with data

insert into demo.orders (user_id, shop_id, status)
select
    (ids.id % 10) + 1 as user_id,
    (ids.id % 4) + 1 as shop_id,
    1 as status
from generate_series(1, 10000) ids (id);

insert into demo.order_item (order_id, price, amount, sku)
select
    id as order_id,
    (random() + 1) * 1000.0 as price,
    (random() * 10) + 1 as amount,
    md5(random()::text) as sku
from demo.orders;

insert into demo.order_item (order_id, price, amount, sku)
select
    id as order_id,
    (random() + 1) * 2000.0 as price,
    (random() * 5) + 1 as amount,
    md5((random() + 1)::text) as sku
from demo.orders where id % 2 = 0;

-- collecting statistics
vacuum analyze demo.orders, demo.order_item;

-- updating the status of several orders
update demo.orders
set status = 2 -- paid order
where
    status = 1 -- new order
  and id in (
    select id from demo.orders where id % 4 = 0 order by id limit 10000);

update demo.order_item
set warehouse_id = case when order_id % 8 = 0 then 1 else 2 end
where
    warehouse_id is null
  and order_id in (
    select id from demo.orders
    where
        status = 2
      and created_at >= current_timestamp - interval '1 day');

-- collecting statistics
vacuum analyze demo.orders, demo.order_item;

-- For partitioned tables

create table if not exists demo.orders_partitioned(
    id         bigint not null generated always as identity,
    user_id    bigint      not null,
    shop_id    bigint      not null,
    status     int         not null,
    created_at timestamptz not null default current_timestamp,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.orders_default
    partition of demo.orders_partitioned default;

create table if not exists demo.order_item_partitioned(
    id           bigint generated always as identity,
    order_id     bigint         not null,
    created_at   timestamptz    not null,
    price        decimal(22, 2) not null default 0,
    amount       int            not null default 0,
    sku          varchar(255)   not null,
    warehouse_id int,
    primary key (id, created_at),
    constraint fk_order_item_order_id foreign key (order_id, created_at)
        references demo.orders_partitioned (id, created_at)
) partition by range (created_at);

create index if not exists idx_order_item_partitioned_order_id
    on demo.order_item_partitioned (order_id);

create index if not exists idx_order_item_partitioned_warehouse_id_without_nulls
    on demo.order_item_partitioned (warehouse_id) where warehouse_id is not null;

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;

-- Filling with data

insert into demo.orders_partitioned (user_id, shop_id, status)
select (ids.id % 10) + 1 as user_id,
       (ids.id % 4) + 1  as shop_id,
       1                 as status
from generate_series(1, 10000) ids (id);

insert into demo.order_item_partitioned (order_id, created_at, price, amount, sku)
select id as order_id, created_at,
       (random() + 1) * 1000.0 as price,
       (random() * 10) + 1     as amount,
       md5(random()::text)     as sku
from demo.orders_partitioned;

insert into demo.order_item_partitioned (order_id, created_at, price, amount, sku)
select id as order_id, created_at,
       (random() + 1) * 2000.0   as price,
       (random() * 5) + 1        as amount,
       md5((random() + 1)::text) as sku
from demo.orders_partitioned
where id % 2 = 0;

-- collecting statistics
vacuum analyze demo.orders_partitioned, demo.order_item_partitioned;

-- updating the status of several orders
update demo.orders_partitioned
set status = 2 -- paid order
where status = 1 -- new order
  and id in (select id
             from demo.orders_partitioned
             where id % 4 = 0
             order by id
             limit 10000);

update demo.order_item_partitioned
set warehouse_id = case when order_id % 8 = 0 then 1 else 2 end
where warehouse_id is null
  and order_id in (select id
                   from demo.orders_partitioned
                   where status = 2
                     and created_at >= current_timestamp - interval '1 day');

-- collecting statistics
vacuum analyze demo.orders_partitioned, demo.order_item_partitioned;
```

## How to fix

It is necessary to monitor bloat and regularly rebuild indexes using the [reindex concurrently](https://postgrespro.ru/docs/postgresql/current/sql-reindex) command.
