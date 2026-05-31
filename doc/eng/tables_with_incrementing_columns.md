# Check for tables with incrementing column names

Tables that contain groups of columns sharing a common base name followed by a sequential integer
suffix (for example, `phone1`, `phone2`, `phone3` or `address1`, `address2`, `address3`) are a sign
of de-normalization: multiple values of the same concept are stored as separate columns instead of
being extracted into a dedicated child table linked by a foreign key.

This pattern is often called a **repeating group** and violates the First Normal Form (1NF).
It creates several practical problems:

- Adding a new value (e.g., `phone4`) requires a schema migration.
- Querying all values requires enumerating column names explicitly.
- Constraints (uniqueness, not-null, references) must be repeated for each numbered column.
- Searching across all values requires `OR` or `UNION` constructs instead of a simple index lookup.

The check reports any table where two or more columns share the same non-numeric prefix.

Similar to [SchemaCrawler's `LinterTableWithIncrementingColumns`](https://www.schemacrawler.com/lint.html).

## SQL query

- [tables_with_incrementing_columns.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_incrementing_columns.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.orders (
    id       bigint generated always as identity primary key,
    phone1   text,
    phone2   text,
    address1 text,
    address2 text,
    address3 text,
    created_at timestamptz,
    created_by text,
    sku1 text,
    "updatedAt" timestamptz,
    "updatedBy" text
);

create table if not exists demo.events (
    id         bigint    not null,
    event_date date      not null,
    tag1       text,
    tag2       text
) partition by range (event_date);

create table if not exists demo.events_2024
    partition of demo.events
        for values from ('2024-01-01') to ('2025-01-01');
```

## How to fix

Extract the repeating group into a separate child table and link it back with a foreign key.

```sql
-- Before: repeating columns in the parent table
-- orders.phone1, orders.phone2, ...

-- After: a dedicated child table
create table demo.order_phones (
    id       bigint generated always as identity primary key,
    order_id bigint not null references demo.orders (id) on delete cascade,
    phone    text   not null
);
```
