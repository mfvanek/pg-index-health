# Check for primary keys with a fixed-length varchar type of 32, 36, or 38 characters

## Why you should pay attention to such primary keys?

Using the `varchar` type to store GUIDs/UUIDs in the database is an anti-pattern.

PostgreSQL has a built-in [uuid](https://www.postgresql.org/docs/current/datatype-uuid.html) type,
which should be used to store all such identifiers.
It has a more compact representation and a more efficient implementation.

## Ways to represent a uuid in text form

```
b9b1f6f5-7f90-4b68-a389-f0ad8bb5784b - 36 characters
b9b1f6f57f904b68a389f0ad8bb5784b - 32 characters (without hyphens)
{b9b1f6f5-7f90-4b68-a389-f0ad8bb5784b} - 38 characters (with curly braces)
```

## SQL query

- [primary_keys_with_varchar.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/primary_keys_with_varchar.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo."t-varchar-short" (
 "id-short" varchar(32) not null primary key
);

insert into demo."t-varchar-short" values (replace(gen_random_uuid()::text, '-', ''));
select "id-short" from demo."t-varchar-short";
select "id-short"::uuid from demo."t-varchar-short";

create table if not exists demo.t_varchar_long (
    id_long varchar(36) not null primary key
);

insert into demo.t_varchar_long values (gen_random_uuid());
select id_long from demo.t_varchar_long;

create table if not exists demo.t_link (
    "id-short" varchar(32) not null references demo."t-varchar-short" ("id-short"),
    id_long varchar(36) not null references demo.t_varchar_long (id_long),
    primary key (id_long, "id-short")
);

create table if not exists demo.t_varchar_long_not_pk (
    id_long varchar(36) not null
);

create table if not exists demo.t_uuid (
    id uuid not null primary key
);

insert into demo.t_uuid values (gen_random_uuid());
select id from demo.t_uuid;
```

## How to fix

Convert the columns that store UUIDs from the `varchar` type to the built-in `uuid` type.

If the column already contains valid UUIDs, the type can be changed with an explicit cast:

```sql
alter table demo.t_varchar_long
    alter column id_long type uuid using id_long::uuid;
```

For values without hyphens (32 characters), PostgreSQL will also correctly perform the cast to `uuid`.

Take dependencies into account: if foreign keys reference such a primary key, the type must be changed consistently
across all related columns. The usual order of actions is:

1. drop the foreign keys that reference the column;
2. change the type of the primary and foreign key columns to `uuid`;
3. recreate the foreign keys.

On large tables, `alter column ... type` rewrites the table under a lock and can take a long time —
plan the migration in advance.
