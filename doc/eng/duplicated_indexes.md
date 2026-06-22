# Check for duplicated indexes in tables

## Why index duplicates can appear

The index on a primary key and indexes on unique constraints are created [automatically](https://www.postgresql.org/docs/17/indexes-unique.html).
If indexes of this kind were also created manually, then [duplicates result](https://www.postgresql.org/docs/17/sql-createindex.html).
PostgreSQL is not able to track such situations and prevent the creation of duplicates.

## Why you should get rid of duplicated indexes

When data changes, [every index](https://www.postgresql.org/docs/17/btree.html) is updated.
Thus, duplicates degrade performance, take up disk space, and
require regular maintenance (vacuuming, reindexing when bloated).

## SQL query

- [duplicated_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_indexes.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

-- For ordinary (non-partitioned) tables

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
    sku varchar(255) not null unique, -- the index will be created automatically
    warehouse_id int
);

-- a duplicate that can (and should) be removed
create unique index if not exists idx_order_item_sku on demo.order_item (sku);

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
    unique (sku, created_at),
    constraint fk_order_item_order_id foreign key (order_id, created_at)
      references demo.orders_partitioned (id, created_at)
) partition by range (created_at);

-- a duplicate that can (and should) be removed
create unique index if not exists idx_order_item_partitioned_sku on demo.order_item_partitioned (sku, created_at);

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## How to fix

Choose the index you want to keep and drop the duplicate.
