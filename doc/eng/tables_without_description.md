# Check for tables without description

## Why you should add descriptions to tables

It is easier for developers to figure out which tables to get the needed data from.
Having descriptions for tables simplifies their maintenance and modification in the future.

## SQL query

- [tables_without_description.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_description.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.table_without_description(
    id integer not null primary key,
    first_name text,
    last_name text
);

comment on table demo.table_without_description is '   ';

create table if not exists demo.table_without_description_partitioned(
    id integer not null primary key,
    first_name text,
    last_name text
) partition by range (id);

comment on table demo.table_without_description_partitioned is '';

create table if not exists demo."table_without_description_partitioned_1_10"
    partition of demo.table_without_description_partitioned
        for values from (1) to (10);
```

## How to fix

Add a meaningful table description using the `comment on table` command.

```sql
comment on table demo.table_without_description is 'Stores user data';
```

Note that an empty or whitespace-only comment is treated as missing —
the description must carry meaningful content.
