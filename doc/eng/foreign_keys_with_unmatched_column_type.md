# Check for foreign keys that have mismatched column types

## Why the column types of foreign keys must match

The column types in the referencing and the target relation must match.
A column of type `integer` must reference a column of type `integer`.
This eliminates unnecessary conversions at the DBMS level and in the application code, and reduces the number of errors
that may arise from type mismatches in the future.

One might argue that it is enough for the column types in the referencing relation to be compatible and no smaller than in the target relation.
For example, a column of type `bigint` can reference a column of type `integer`.
**This should not be done.**
You must take into account that different types will leak into the application code, especially with automatic model generation.

## SQL query

- [foreign_keys_with_unmatched_column_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_with_unmatched_column_type.sql)

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
    order_id int not null references demo.orders (id),
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
    order_id     int         not null,
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

Align the column types in the referencing and the target relation.
