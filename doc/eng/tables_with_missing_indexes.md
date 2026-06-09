# Check tables for missing indexes

## How to tell that an index is needed

The pg_stat_all_tables view [contains statistics on table access](https://www.postgresql.org/docs/current/monitoring-stats.html#MONITORING-PG-STAT-ALL-INDEXES-VIEW).
From it you can obtain data on how often sequential scans and index lookups are used.
If a table is frequently scanned sequentially, it is worth adding indexes for the relevant columns.

## Why you should monitor index usage

If the statistics show many queries with sequential table scans,
adding an index can speed up the search and reduce the load on the database.

## SQL query

- [tables_with_missing_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_missing_indexes.sql)

## Check type

- **runtime** (requires accumulated statistics)

In a database cluster, the check must be performed on every host.

## Support for partitioned tables

Supports partitioned tables. The check is performed on each partition.

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

-- Filling with data

insert into demo.orders (user_id, shop_id, status)
select
    (ids.id % 10) + 1 as user_id,
    (ids.id % 4) + 1 as shop_id,
    1 as status
from generate_series(1, 10000) ids (id);

DO $$
    DECLARE
        min_shop_id bigint;
        r record;
    BEGIN
        -- compute the minimum shop_id once
        SELECT MIN(shop_id) INTO min_shop_id FROM demo.orders;

        -- loop 500 times
        FOR i IN 1..500 LOOP
                SELECT *
                INTO r
                FROM demo.orders
                WHERE shop_id = min_shop_id;

                SELECT *
                INTO r
                FROM demo.orders
                WHERE shop_id = 12345;
            END LOOP;
    END $$;

-- collect statistics
vacuum analyze demo.orders;

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

-- Filling with data

insert into demo.orders_partitioned (user_id, shop_id, status)
select (ids.id % 10) + 1 as user_id,
       (ids.id % 4) + 1  as shop_id,
       1                 as status
from generate_series(1, 10000) ids (id);

DO $$
    DECLARE
        min_shop_id bigint;
        r record;
    BEGIN
        -- compute the minimum shop_id once
        SELECT MIN(shop_id) INTO min_shop_id FROM demo.orders_partitioned;

        -- loop 500 times
        FOR i IN 1..500 LOOP
                SELECT *
                INTO r
                FROM demo.orders_partitioned
                WHERE shop_id = min_shop_id;

                SELECT *
                INTO r
                FROM demo.orders_partitioned
                WHERE shop_id = 12345;
            END LOOP;
    END $$;

-- collect statistics
vacuum analyze demo.orders_partitioned;
```

## How to fix

Analyze the queries against the problematic table and add an index on the columns that are most frequently used for filtering.
In the example above, the search is done by `shop_id`, so the index should be created on it.

```sql
create index concurrently if not exists idx_orders_shop_id
    on demo.orders (shop_id);
```

Use `create index concurrently` so that writes to the table are not blocked while the index is being created.

This check only signals a large number of sequential scans — it does not indicate
exactly which index is needed. Choose the composition and type of the index based on real queries (for example, using `explain analyze`).
Keep in mind that redundant indexes slow down writes, so add only the ones that are truly necessary.
