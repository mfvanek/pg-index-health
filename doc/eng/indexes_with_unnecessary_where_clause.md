# Check for indexes that have a redundant predicate with a where clause

This check is closely related to the [indexes_with_null_values](indexes_with_null_values.md) check.

It happens that developers start copying and editing index creation queries.
This leads to queries with predicates of the form `where <column> is not null` appearing even on columns
that already have the `not null` characteristic.

Such indexes:
- contain a redundant predicate that is evaluated every time;
- cannot be used as the basis for a foreign key if they are created with the `unique` keyword.

## SQL query

- [indexes_with_unnecessary_where_clause.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_unnecessary_where_clause.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.t1(
    id     bigint not null primary key,
    id_ref bigint not null);

create index if not exists idx_t1_id_ref on demo.t1 (id_ref) where id_ref is not null;

create table if not exists demo.t2(
    "first-ref" bigint not null,
    second_ref  bigint not null,
    t1_id       bigint references demo.t1 (id));

create index if not exists "idx_t2_first-ref_second_ref" on demo.t2 (second_ref, "first-ref") where "first-ref" is not null;

create index if not exists idx_t2_id_ref on demo.t2 (t1_id) where t1_id is not null;

create index if not exists idx_second_ref_t1_id on demo.t2 (t1_id, second_ref) where t1_id is not null;

create table if not exists demo.one_partitioned(
    "first-ref" bigint not null,
    second_ref  bigint not null
) partition by range (second_ref);

create index if not exists "idx_second_ref_first-ref" on demo.one_partitioned (second_ref, "first-ref") where "first-ref" is not null;

create table if not exists demo.one_default partition of demo.one_partitioned default;
```

## How to fix

Remove the redundant where predicates from the indexes.
