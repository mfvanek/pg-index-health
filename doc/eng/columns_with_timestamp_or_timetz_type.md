# Check for columns with the timestamp (without time zone) or timetz types in tables

## Why you should not use them

- Do not use the `timestamp` type to store timestamps.
  Instead, use `timestamptz` (also known as `timestamp with time zone`).
- Do not use the `timetz` type. Most likely, you need `timestamptz` instead.

See also:

* https://habr.com/ru/articles/772954/
* https://neon.com/postgresql/postgresql-tutorial/postgresql-timestamp
* https://wiki.postgresql.org/wiki/Don't_Do_This#Don.27t_use_timestamp_.28without_time_zone.29

## SQL query

- [columns_with_timestamp_or_timetz_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_timestamp_or_timetz_type.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo."bad-time"
(
    id bigint generated always as identity primary key,
    "created-bad" timestamp not null default now(),
    created_at timestamptz not null default now(),
    created_at_good timestamp with time zone not null default now(),
    time_bad timetz not null
);

create table if not exists demo."bad-time_partitioned"
(
    id uuid not null primary key,
    "created-bad" timestamp not null default now(),
    created_at timestamptz not null default now(),
    created_at_good timestamp with time zone not null default now(),
    time_bad timetz not null
) partition by hash (id);

create table if not exists demo."bad_time_partitioned_hash_p0"
    partition of demo."bad-time_partitioned" for values with (modulus 4, remainder 0);
```

## How to fix

Change the column type to `timestamptz`. Do not forget to make the necessary changes in the application working with the database.
