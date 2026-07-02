# Check for unlogged tables

Unlogged tables are not backed by the Write-Ahead Log (WAL), so data written to them
is not replicated to standby servers and will be truncated automatically after a server crash.
They are unsuitable for storing persistent data.

See also https://www.postgresql.org/docs/current/sql-createtable.html#SQL-CREATETABLE-UNLOGGED

## SQL query

- [unlogged_tables.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unlogged_tables.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Applicable to both regular and partitioned unlogged tables.

## Reproduction script

```sql
create schema if not exists demo;

create unlogged table demo.unlogged_table(
    id   bigint generated always as identity primary key,
    info text
);
```

## How to fix

Convert the unlogged table to a regular (logged) table:

```sql
alter table demo.unlogged_table set logged;
```

If the table was intentionally created as unlogged for performance reasons (e.g., staging or temporary data),
consider using a temporary table instead, which is more explicit about its transient nature:

```sql
create temporary table tmp_staging(
    id   bigint generated always as identity primary key,
    info text
);
```
