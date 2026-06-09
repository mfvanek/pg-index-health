# Check for primary keys with a serial type

## Why a primary key should not be created with the serial type

A primary key of the serial type [causes problems](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_serial):

- it does not comply with the SQL standard, which means the code cannot be reused;
- it can cause errors if table manipulations are included in deployment scripts;
- it is hard to make changes to a primary key of this type.

There is [another way to create a primary key](https://www.postgresql.org/docs/current/sql-createtable.html#SQL-CREATETABLE-PARMS-GENERATED-IDENTITY).
That is exactly what you should use.

## SQL query

- [primary_keys_with_serial_types.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/primary_keys_with_serial_types.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

-- Bad: a primary key with the bigserial type — the check will report it.
create table if not exists demo.bad_accounts (
    id bigserial not null primary key,
    name varchar(255) not null
);

-- Good: a primary key via generated always as identity — the check will not report it.
create table if not exists demo.good_accounts (
    id bigint not null generated always as identity primary key,
    name varchar(255) not null
);

-- A partitioned table with a serial primary key.
-- The check will report only the parent table; partitions are ignored.
create table if not exists demo.bad_partitioned (
    id bigserial not null,
    created_at timestamptz not null default now(),
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.bad_partitioned_default
    partition of demo.bad_partitioned default;
```

## How to fix

Replace `serial`/`bigserial` with the modern `generated always as identity` syntax.

For new tables, simply declare the primary key like this:

```sql
create table demo.good_accounts (
    id bigint not null generated always as identity primary key,
    name varchar(255) not null
);
```

An existing serial column can be migrated to identity without recreating the table.
The column type (`integer`/`bigint`) does not change in this case — serial merely adds a default value
from a sequence and ownership of that sequence:

```sql
-- 1. Remove the default that references the sequence
alter table demo.bad_accounts
    alter column id drop default;

-- 2. Drop the now-orphaned sequence
drop sequence demo.bad_accounts_id_seq;

-- 3. Make the column an identity column
alter table demo.bad_accounts
    alter column id add generated always as identity;

-- 4. Synchronize the internal identity counter with the current maximum
select setval(pg_get_serial_sequence('demo.bad_accounts', 'id'), coalesce(max(id), 1))
from demo.bad_accounts;
```

The default sequence name is `<table>_<column>_seq`; you can determine it via
`pg_get_serial_sequence('demo.bad_accounts', 'id')`.
