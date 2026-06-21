# Check for tables that use inheritance

Inheritance is an outdated and unfortunate mechanism. It should not be used when designing modern databases.
Instead of inheritance, consider other ways of organizing tables, for example, partitioning.

See also https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_table_inheritance

## SQL query

- [tables_with_inheritance.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_inheritance.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Not applicable to partitioned tables. Inheritance and partitioning cannot be used together.

## Reproduction script

```sql
create schema if not exists demo;

create table demo.parent_table(
    id   bigint generated always as identity primary key,
    info text
);

create table demo.child_table(
    extra_info text
) inherits (demo.parent_table);

create table demo."second-child_table"(
    extra_info2 text
) inherits (demo.child_table);

create table if not exists demo.one_partitioned(
    ref_type bigserial not null primary key
) partition by range (ref_type);

create table if not exists demo.one_default
    partition of demo.one_partitioned default;
```

## How to fix

Abandon table inheritance and redesign the schema.

If inheritance was used to split a large table into parts, switch to
[declarative partitioning](https://www.postgresql.org/docs/current/ddl-partitioning.html).

If inheritance was only needed to reuse a common set of columns,
move the shared data into a single table, and the specific data into related tables (via foreign keys).

You can break an existing inheritance relationship without recreating the table using the `no inherit` command:

```sql
alter table demo.child_table
    no inherit demo.parent_table;
```

After this, the child table becomes independent and will no longer appear in the check results.
