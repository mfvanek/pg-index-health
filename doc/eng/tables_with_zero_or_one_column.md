# Check for tables with zero or one column

## Why you should track such tables

This usually indicates a poor table design in the database or the presence of garbage.
Tables without columns should be deleted permanently.
Tables with a single column make sense to redesign and extend, or to merge with another table.

If you really need a table with a single column, for example,
as a global index for partitioned tables, just ignore the results of this check.

## SQL query

- [tables_with_zero_or_one_column.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_zero_or_one_column.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.empty(
);

create table if not exists demo.one(
    ref_type integer not null primary key
);

create table if not exists demo.two(
    ref_type integer not null primary key,
    description text
);

create table if not exists demo.one_partitioned(
    ref_type bigserial not null primary key
) partition by range (ref_type);

create table if not exists demo.one_default partition of demo.one_partitioned default;
```

## How to fix

Tables without columns are almost always garbage — drop them.

```sql
drop table demo.empty;
```

Tables with a single column make sense to redesign: add the missing columns

```sql
alter table demo.one
    add column description text;
```

or merge such a table with another one it logically belongs to.

If a single-column table is needed intentionally (for example, as a global index for partitioned tables),
exclude it from the check using a suitable predicate (`SkipTablesByNamePredicate`, etc.).
