# Check for intersected (overlapping) indexes in tables

## How index intersection arises

A composite index can be used in conditions [with any subset of the index columns](https://www.postgresql.org/docs/17/indexes-multicolumn.html).
For example, an index on columns A+B is just as efficient for searching by column A as an index on column A alone.
Keep in mind, though, that for a composite btree index the search is efficient only when the leading (leftmost) columns of the composite index are part of the condition.

## Why you should get rid of intersected indexes

When data changes, [every index](https://www.postgresql.org/docs/17/btree.html) is updated,
so redundant indexes degrade performance.

## SQL query

- [intersected_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/intersected_indexes.sql)

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

create index if not exists idx_order_item_sku on demo.order_item (sku);

create index if not exists idx_order_item_sku_warehouse_id on demo.order_item (sku, warehouse_id);

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

create index if not exists idx_order_item_partitioned_sku
    on demo.order_item_partitioned (sku, created_at);

create index if not exists idx_order_item_partitioned_sku_warehouse_id
    on demo.order_item_partitioned (sku, created_at, warehouse_id);

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## How to fix

From each pair of intersected indexes you should drop the "redundant" one — the index whose columns form a leading (leftmost) subset of the other index's columns.
Such an index is redundant, since the queries that relied on it are served efficiently by the wider composite index.

In the script above, the index `idx_order_item_sku` on `(sku)` is fully covered by the index `idx_order_item_sku_warehouse_id` on `(sku, warehouse_id)`, so it can be dropped:

```sql
drop index concurrently if exists demo.idx_order_item_sku;
```

For ordinary tables use [drop index concurrently](https://www.postgresql.org/docs/17/sql-dropindex.html)
so that the removal does not block writes to the table.

For partitioned tables, drop the index on the partitioned (parent) table itself — it will be removed on all partitions in a cascading manner.
The `concurrently` option is not supported for partitioned indexes:

```sql
drop index if exists demo.idx_order_item_partitioned_sku;
```

Before dropping, make sure the index being removed is not used on its own (for example, as a unique index or as an index backing a constraint),
and that the wider index really covers all the required search scenarios.
