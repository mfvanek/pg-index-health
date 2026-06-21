# Check for columns without a description

## Why you should add a description to columns

A description of columns makes the business meaning of the values stored there clear.
A developer/analyst/tester will make fewer mistakes and do their work faster
if they understand what constraints exist on the data in the columns from the business side and where they come from (what their purpose is).
Having a description for columns makes them easier to maintain and modify in the future.

## SQL query

- [columns_without_description.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_without_description.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.column_without_description(
    id integer not null primary key,
    ref_type integer,
    ref_value varchar(64)
);

comment on column demo.column_without_description.ref_value is '   ';

create table if not exists demo.column_without_description_partitioned(
    id integer not null primary key,
    ref_type integer,
    ref_value varchar(64)
) partition by range (id);

comment on column demo.column_without_description_partitioned.ref_type is '';

create table if not exists demo.column_without_description_partitioned_1_10
    partition of demo.column_without_description_partitioned
        for values from (1) to (10);

comment on column demo."column_without_description_partitioned_1_10".ref_value is '';
```

## How to fix

Add human-readable descriptions to all columns.
