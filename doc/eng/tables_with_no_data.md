# Check for tables with no data

Identifies tables that contain no data, based on storage statistics from `pg_catalog`.

For **regular tables**, the check uses `relpages = 0` as the signal — meaning no data pages have been allocated.

For **partitioned tables**, the `relpages` of the parent table is always zero (data lives exclusively in partitions),
so the check uses `pg_partition_tree()` to sum `relpages` across all leaf partitions.
A partitioned table is considered empty only when all its leaf partitions have no allocated data pages.

> **Note:** tables from which rows were deleted but not yet vacuumed will still have `relpages > 0` and will not be detected by this check.

See also [SchemaCrawler `LinterTableEmpty`](https://www.schemacrawler.com/lint.html) for a similar approach.

## SQL query

- [tables_with_no_data.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_no_data.sql)

## Check type

- **runtime** (requires accumulated statistics)

## Support for partitioned tables

Supports partitioned tables.
The check aggregates storage statistics across all leaf partitions using `pg_partition_tree()`.
Individual partitions (descendants) are not checked separately.

## Reproduction script

```sql
create schema if not exists demo;

-- Regular tables

create table demo.regular_empty
(
    id  bigint primary key,
    val text
);

create table demo.regular_with_data
(
    id  bigint primary key,
    val text
);
insert into demo.regular_with_data (id, val) values (1, 'hello');

create table demo.regular_deleted_no_vacuum
(
    id  bigint primary key,
    val text
);
insert into demo.regular_deleted_no_vacuum (id, val) values (1, 'to be deleted');
analyze demo.regular_deleted_no_vacuum;
delete from demo.regular_deleted_no_vacuum;
-- vacuum analyze demo.regular_deleted_no_vacuum;

-- Single-level partitioned tables

create table demo.partitioned_no_parts
(
    id  bigint,
    val text
) partition by range (id);

create table demo.partitioned_empty_parts
(
    id  bigint,
    val text
) partition by range (id);

create table demo.partitioned_empty_parts_p1 partition of demo.partitioned_empty_parts for values from (1) to (100);
create table demo.partitioned_empty_parts_p2 partition of demo.partitioned_empty_parts for values from (100) to (200);

create table demo.partitioned_with_data
(
    id  bigint,
    val text
) partition by range (id);

create table demo.partitioned_with_data_p1 partition of demo.partitioned_with_data for values from (1) to (100);
create table demo.partitioned_with_data_p2 partition of demo.partitioned_with_data for values from (100) to (200);

insert into demo.partitioned_with_data (id, val) values (50, 'hello');
analyze demo.partitioned_with_data;

-- Two-level partitioned tables (sub-partitioning)

create table demo.two_level_empty
(
    id     bigint,
    region text,
    val    text
) partition by range (id);

create table demo.two_level_empty_2020 partition of demo.two_level_empty for values from (1) to (1000) partition by list (region);
create table demo.two_level_empty_2020_us partition of demo.two_level_empty_2020 for values in ('us');
create table demo.two_level_empty_2020_eu partition of demo.two_level_empty_2020 for values in ('eu');

create table demo.two_level_with_data
(
    id     bigint,
    region text,
    val    text
) partition by range (id);

create table demo.two_level_with_data_2020 partition of demo.two_level_with_data for values from (1) to (1000) partition by list (region);
create table demo.two_level_with_data_2020_us partition of demo.two_level_with_data_2020 for values in ('us');
create table demo.two_level_with_data_2020_eu partition of demo.two_level_with_data_2020 for values in ('eu');

insert into demo.two_level_with_data (id, region, val) values (50, 'us', 'hello');
analyze demo.two_level_with_data;
```

## How to fix

Review tables identified by this check and determine whether they are intentionally empty.
Tables that were created but never populated may indicate dead or unused schema objects
and can be candidates for removal.
