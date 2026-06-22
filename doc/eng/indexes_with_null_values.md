# Check for indexes that include null values

## Features of creating b-tree indexes

By default, Postgres [includes null values in btree indexes](https://www.postgresql.org/docs/17/indexes-ordering.html).

## Why you should remove null values from indexes

This can significantly reduce the index size in the case where a null value occurs frequently.
A partial index, from which null values are excluded, will be more optimal, because when searching for
a common value [the index will not be used anyway](https://www.postgresql.org/docs/17/indexes-partial.html).
The search will be faster. The index will take up less space on disk.

## SQL query

- [indexes_with_null_values.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_null_values.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create sequence if not exists demo.accounts_seq;

create table if not exists demo.accounts (
    id bigint not null primary key default nextval('demo.accounts_seq'),
    client_id bigint not null,
    account_number varchar(50) not null unique,
    account_balance numeric(22,2) not null default 0,
    deleted_at timestamptz
);

create index if not exists i_accounts_deleted_at
    on demo.accounts (deleted_at);

create unique index if not exists i_accounts_account_number_deleted_at
    on demo.accounts (account_number, deleted_at);

create table if not exists demo.dict(
    ref_type int not null primary key,
    description text
);

create table if not exists demo.partitioned_table(
    ref_value varchar(64) not null,
    ref_type bigserial not null references demo.dict(ref_type),
    creation_date timestamp with time zone not null,
    entity_id varchar(64) not null,
    deleted_at timestamptz,
    primary key (ref_value, ref_type, creation_date, entity_id)
) partition by range (creation_date);

create index if not exists idx_t1_deleted_at on demo.partitioned_table(deleted_at);

create table if not exists demo.t1_default
    partition of demo.partitioned_table default;
```

## How to fix

Use partial indexes to exclude null values.
