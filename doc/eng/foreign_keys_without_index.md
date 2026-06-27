# Check for foreign keys without an index

## Features of creating foreign keys

When a [foreign key constraint is created](https://www.postgresql.org/docs/17/ddl-constraints.html#DDL-CONSTRAINTS-FK),
an index on the column (or group of columns) with the foreign key is **not** created automatically.
There is also no requirement to create such an index manually.

## Why you should create foreign keys with indexes

If a column with a foreign key has no index, then when searching for rows with the same foreign key
a sequential scan of the entire table is performed, which decreases performance.
Also, when deleting data from the main table, a referential integrity check is performed,
which also causes a sequential scan of the related table with the foreign key that has no index on it.
At the same time, you need to assess how often a search or modification of data by this column happens and the size of the table itself.
If the search is performed rarely and/or the table containing the column with the foreign key is small,
then adding an index may not improve performance.

## SQL query

- [foreign_keys_without_index.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_without_index.sql)

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
    sku varchar(255) not null,
    warehouse_id int
);

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

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## How to fix

Create the missing indexes on the foreign keys.
If you are 100% sure that you do not need such an index, record it as an exception.
