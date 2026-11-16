# Check for columns with an enum type in tables

## Why you should not use it

[Enumerated types](https://www.postgresql.org/docs/current/datatype-enum.html) look convenient, but they are hard to evolve:

- New values can be added to an enum, but existing values cannot be removed or reordered.
- Removing a value requires recreating the type and rewriting all dependent columns and indexes.
- Any change to the list of allowed values is a DDL migration rather than a simple data update.
- Enums are less flexible than foreign keys to lookup tables: you cannot attach additional attributes to a value or mark it as obsolete.

It is better to use a `text` column with a `check` constraint or a foreign key to a lookup table.

The check also finds columns that are arrays of enums (including multidimensional ones).

See also the [prefer-text-with-check-to-enum](https://github.com/kristiandupont/schemalint/blob/main/src/rules/README.md#prefer-text-with-check-to-enum) rule in schemalint.

## SQL query

- [columns_with_enum_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_enum_type.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create type demo.order_status as enum ('new', 'paid', 'shipped');

create table if not exists demo."bad-orders"
(
    id bigint generated always as identity primary key,
    "status-bad" demo.order_status not null, -- enum
    status_history demo.order_status[], -- array of enums
    status_text text not null check (status_text in ('new', 'paid', 'shipped')) -- recommended alternative
);

-- An index on an enum column is not reported
create index if not exists i_bad_orders_status on demo."bad-orders" ("status-bad");

-- Views over enum columns are not reported
create or replace view demo.v_bad_orders as select id, "status-bad" from demo."bad-orders";

-- For partitioned tables

create table if not exists demo."bad-orders_partitioned"
(
    id bigint not null,
    created_at timestamptz not null,
    "status-bad" demo.order_status not null,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo."bad-orders_partitioned_default"
    partition of demo."bad-orders_partitioned" default;
```

## How to fix

Change the type of the target column to `text` and restrict the allowed values with a `check` constraint:

```sql
-- Views that depend on the column have to be dropped first and recreated afterwards
drop view if exists demo.v_bad_orders;

alter table demo."bad-orders"
    alter column "status-bad" type text using "status-bad"::text,
    add constraint c_bad_orders_status_valid check ("status-bad" in ('new', 'paid', 'shipped'));
```

If the list of values changes often or values need additional attributes, use a foreign key to a lookup table instead.
Once no column uses the enum type anymore, drop it with `drop type`.
Do not forget to make the necessary changes in the application working with the database.
