# Check for tables not linked to other tables

## Why tables without links should be tracked

A lack of links in a table may be the result of a mistake: forgetting to add foreign keys.
All tables in the database reflect the data model — links are needed for consistent changes across tables.
Foreign keys also make it possible to quickly and correctly find data related to a single entity.

If tables without links were created intentionally, they should simply be ignored.

## SQL query

- [tables_not_linked_to_others.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_not_linked_to_others.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

-- Linked tables: clients and accounts are connected by a foreign key,
-- so the check will not report them.
create table if not exists demo.clients (
    id bigint not null primary key generated always as identity,
    last_name varchar(255) not null,
    first_name varchar(255) not null
);

create table if not exists demo.accounts (
    id bigint not null primary key generated always as identity,
    client_id bigint not null references demo.clients (id),
    account_number varchar(50) not null unique
);

-- A table without a single link to other tables — it will appear in the check results.
create table if not exists demo.standalone (
    id bigint not null primary key generated always as identity,
    name varchar(255) not null
);

-- A partitioned table without links.
-- The check will report only the parent table; partitions are ignored.
create table if not exists demo.entity_reference(
    ref_type varchar(32) not null,
    ref_value varchar(64) not null,
    creation_date timestamptz not null,
    entity_id varchar(64) not null,
    primary key (ref_type, ref_value, creation_date, entity_id)
) partition by range (creation_date);

create table if not exists demo.entity_reference_default
    partition of demo.entity_reference default;
```

## How to fix

Make sure the absence of links is not a modeling mistake (a forgotten foreign key).
If the table is supposed to reference another entity, add a foreign key.

```sql
alter table demo.standalone
    add column client_id bigint not null references demo.clients (id);
```

If a table without links was created intentionally (for example, a dictionary, a log, or a service table),
just exclude it from the check using a suitable predicate (`SkipTablesByNamePredicate`, etc.).
