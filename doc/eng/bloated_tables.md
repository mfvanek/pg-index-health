# Check for table bloat

## Why tables become bloated

Frequent UPDATE and DELETE operations can cause a noticeable increase in table size,
because old row versions [are not removed immediately](https://www.postgresql.org/docs/17/routine-vacuuming.html).
Non-blocking cleanup marks these obsolete versions as deleted, and they can later be reused for adding new rows,
but the physical space is returned to the system only if these deleted rows were at the end of the table.

## Why you should keep an eye on table bloat

Although obsolete records are gradually processed by the autovacuum daemon, the table size will remain too large and the table sparse.
This leads to decreased performance, because scanning the table becomes slower.
Therefore, it is important to track sharp changes in table size if the data is updated frequently.
Data about too rapid growth of a table's size may also indicate that autovacuum is configured incorrectly and these settings need to be changed.

## SQL query

- [bloated_tables.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_tables.sql)

## Check type

- **runtime** (requires accumulated statistics)

## Support for partitioned tables

Partitioned tables are supported. The bloat percentage is calculated for each partition separately.

## How this check works

To run the query, the user needs read permissions on the tables being checked.

### Principle of operation

A SQL query is executed against the tables of the `pg_catalog` system schema. They contain statistical information about the main objects:
tables, indexes, columns.

First, the query gathers data about the tables. It checks whether statistics are available for the table.

Then, based on this data, it determines the size of a single tuple and the total number of pages used by the table.
Next, it estimates the number of pages that the table should use, and compares it with the actual number of pages.
Finally, it calculates the table's bloat in bytes (the difference in pages multiplied by the block size) and as a percentage.
If it exceeds the specified value (10% by default), the table is considered bloated.

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

create index if not exists idx_order_item_order_id
    on demo.order_item (order_id);

create index if not exists idx_order_item_warehouse_id_without_nulls
    on demo.order_item (warehouse_id) where warehouse_id is not null;

-- Populating with data

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

-- collect statistics
vacuum analyze demo.orders, demo.order_item;

-- update the status of several orders
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

-- collect statistics
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

-- Populating with data

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

-- collect statistics
vacuum analyze demo.orders_partitioned, demo.order_item_partitioned;

-- update the status of several orders
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

-- collect statistics
vacuum analyze demo.orders_partitioned, demo.order_item_partitioned;
```

## How to fix

1. Regularly run cleanup (vacuum). Make sure autovacuum is working and its parameters are configured properly.
   Cleanup allows space on pages to be freed efficiently and reused for new row versions.
2. If a particular table in the database can be locked for a long time (from several minutes to several hours depending on the table size),
   then it is acceptable to fully rebuild the table with the [vacuum full](https://www.postgresql.org/docs/18/sql-vacuum.html) command.
   This completely removes the bloat and frees up disk space.
3. If a long lock and the associated downtime are not acceptable,
   then consider using the [pg_repack](https://github.com/reorg/pg_repack) extension.
