# Check for primary keys that are most likely natural keys

## Why you should pay attention to such primary keys?

A natural key is a key formed from attributes that already exist in the real world.
It can give the false impression that a natural key is unique and suitable for use as a table's primary key.
Unfortunately, very often natural keys can lose their uniqueness property over time due to political, legislative, or other changes.
It is preferable to use surrogate (artificial, synthetic) keys.

- [You will regret using natural keys](https://habr.com/ru/articles/819619/)
- [7 Database Design Mistakes to Avoid (With Solutions)](https://www.youtube.com/watch?v=s6m8Aby2at8)

## SQL query

- [primary_keys_that_most_likely_natural_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/primary_keys_that_most_likely_natural_keys.sql)

Surrogate keys must have the type `smallint`, `int`, `bigint`, or `uuid`.
If the primary key contains columns of other types, that is suspicious and requires close attention from the developer.

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.
For partitioned tables the list of allowed column types is wider and includes `date`, `time`, and `timestamp`, which are often used for range partitioning.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.good (
    id int not null primary key
);

create table if not exists demo."times-of-creation" (
    "time-of-creation" timestamptz not null primary key
);

create table if not exists demo.t2_composite (
    passport_series text not null,
    passport_number text not null,
    primary key (passport_series, passport_number)
);

create table if not exists demo.t3_composite (
    app_id uuid not null,
    app_number text not null,
    primary key (app_id, app_number)
);

create table if not exists demo.tp_good (
    creation_date timestamp not null,
    entity_id uuid not null,
    primary key (creation_date, entity_id)
) partition by range (creation_date);

create table if not exists demo."tp_good-default" partition of demo.tp_good default;

create table if not exists demo.tp(
    creation_date timestamp not null,
    ref_type varchar(36) not null,
    entity_id varchar(36) not null,
    primary key (creation_date, ref_type, entity_id)
) partition by range (creation_date);

create table if not exists demo.tp_default partition of demo.tp default;
```

## How to fix

Replace the natural primary key with a surrogate (artificial) one.
To do this, add a new identifier column of type `bigint` (or `uuid`) and make it the primary key,
while preserving the uniqueness of the original "natural" columns via a `unique` constraint, so as not to lose the business rule.

For example, for the `demo.t2_composite` table from the script above:

```sql
-- 1. Add a surrogate key
alter table demo.t2_composite
    add column id bigint generated always as identity;

-- 2. Drop the old primary key on the natural columns
alter table demo.t2_composite
    drop constraint t2_composite_pkey;

-- 3. Make the surrogate column the primary key
alter table demo.t2_composite
    add primary key (id);

-- 4. Preserve the uniqueness of the natural columns (if it is really required)
alter table demo.t2_composite
    add constraint t2_composite_passport_uk unique (passport_series, passport_number);
```

Do not forget to switch all foreign keys that referenced the natural key over to the new surrogate column.

If your database runs online and downtime is not acceptable, perform such changes in stages
(adding the column, gradually populating it, switching references) and use non-blocking operations wherever possible.

Sometimes a natural key is chosen deliberately and is justified (for example, range partitioning by date) — in such cases
the check can be suppressed for a specific table using a skip predicate (see [custom_checks.md](../custom_checks.md)).
