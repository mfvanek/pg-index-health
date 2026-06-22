# Check for indexes in which timestamp columns are not the last in the list

## What is the problem?

It always looks suspicious if a column of an obviously highly variable type such as timestamp/timestamptz is not last in your index.
As a rule, the values of a timestamp field increase monotonically, while the following fields of the index have only a single value at each point in time.
For details, see [the article on Habr](https://habr.com/ru/companies/tensor/articles/488104/).

When creating a composite B-tree index, use the [ESR rule](https://habr.com/ru/articles/911688/) (Equality/Sort/Range):
- **E**quality: first the columns on which the query has `=`;
- **S**ort: then those by which the `order by` sorting goes;
- **R**ange: and only at the end — the columns with ranges `>, <, between`.

## SQL query

- [indexes_with_timestamp_in_the_middle.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_timestamp_in_the_middle.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo."t-multi"
(
    id   int primary key,
    ts   timestamp,
    name text
);

create index idx_multi_mid on demo."t-multi" (id, ts, name);
create index idx_multi_end on demo."t-multi" (id, name, ts);
create index idx_multi_none on demo."t-multi" (id, name);

create index idx_multi_expr_mid on demo."t-multi" (id, date_trunc('day', ts), name);
create index idx_multi_expr_first on demo."t-multi" (date_trunc('day', ts), id, name);

create unique index idx_unique_ts on demo."t-multi" (id, ts, id);

create table if not exists demo.t_part_parent
(
    id       int,
    "ts-bad" timestamptz
) partition by range (id);

create table t_part_p1 partition of demo.t_part_parent for values from (0) to (100);
create table t_part_p2 partition of demo.t_part_parent for values from (100) to (200);

create index idx_part_parent_end on demo.t_part_parent (id, "ts-bad");

create index idx_part_parent_mid on demo.t_part_parent ("ts-bad", id);
```

## How to fix

In accordance with the ESR rule, recreate the index and move the timestamp field to the end of the index.
