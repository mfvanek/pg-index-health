# Check for columns that share the same name but have different data types across tables

Within a single schema, a column with a given name should consistently use the same data type in every table.
When the same column name (for example, `id` or `created_at`) is declared with different types in different tables,
joins and application code become error-prone: implicit casts may be required, indexes may not be used,
and subtle bugs can appear when values are passed between tables.

A classic example is a primary key named `id` that is declared as `bigint` in one table, `int` in another,
and `uuid` in a third. Another common case is a `created_at` column that uses `timestamp` (without time zone)
in some tables and `timestamptz` in others.

This check is similar to [schemacrawler's LinterColumnTypes](https://www.schemacrawler.com/lint.html).

## SQL query

- [columns_with_inconsistent_types.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_inconsistent_types.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

-- Three tables whose primary key is named 'id' but uses three different types: bigint, int and uuid.
-- The 'created_at' column is also inconsistent: timestamp here, timestamptz in the other tables.
create table if not exists demo.clients
(
    id bigint primary key,
    created_at timestamp not null default now()
);

create table if not exists demo.orders
(
    id int primary key,
    created_at timestamptz not null default now()
);

create table if not exists demo.payments
(
    id uuid primary key,
    created_at timestamptz not null default now()
);

-- Partitioned tables participate in the check as well.
-- The parent table is analyzed; its partitions (children) are ignored.
create table if not exists demo.events
(
    id bigint not null,
    created_at timestamptz not null default now(),
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.events_default
    partition of demo.events default;
```

## How to fix

Pick a single canonical type for each column name within the schema and align all tables to it.
For primary keys, prefer `bigint` (or `uuid` when you need globally unique identifiers) and use it consistently.
For timestamps, prefer `timestamptz` everywhere.
Do not forget to make the necessary changes in the application working with the database.
