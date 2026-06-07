# Check for tables where the primary key columns are not first in the column list

## Why you should pay attention to such primary keys?

Placing the primary key as the first column in a table is not a technical necessity,
but it matters as part of style and conventions:

* Readability: the key is immediately visible when looking at the schema; it makes the structure easier to understand.
* Consistency: a single pattern (the **primary key** comes first) reduces "surprises" when working with different schemas.
* Queries: in `select` queries the **primary key** will always be the first column, which simplifies debugging and improves clarity.
* Historical practices: this is an established tradition that helps maintain a consistent style across all tables.

## SQL query

- [tables_where_primary_key_columns_not_first.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_where_primary_key_columns_not_first.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.good_pk (
    product_type text not null,
    description text not null,
    product_subtype text not null,
    primary key (product_type, product_subtype)
);

create table if not exists demo.not_good_pk (
    description text not null,
    product_type text not null,
    product_subtype text not null,
    primary key (product_type, product_subtype)
);

create table if not exists demo."bad-pk" (
    description text not null,
    id bigint generated always as identity primary key
);

create table if not exists demo."bad-pk_partitioned" (
    description text not null,
    id uuid not null primary key,
    created_at timestamptz not null default now()
) partition by hash (id);

create table if not exists demo."bad_pk_partitioned_hash_p0"
    partition of demo."bad-pk_partitioned" for values with (modulus 4, remainder 0);
```

## How to fix

Reorder the columns so that the primary key columns come first.

PostgreSQL does not allow changing the order of columns in an existing table, so the table must be recreated
with the correct column order and the data migrated:

```sql
create table if not exists demo.not_good_pk_new (
    product_type text not null,
    product_subtype text not null,
    description text not null,
    primary key (product_type, product_subtype)
);

insert into demo.not_good_pk_new (product_type, product_subtype, description)
select product_type, product_subtype, description
from demo.not_good_pk;

drop table demo.not_good_pk;
alter table demo.not_good_pk_new rename to not_good_pk;
```

Perform such a migration carefully: for large volumes of data, take locks and execution time into account,
and do not forget to recreate foreign keys, indexes, and other dependent objects.

This is only a stylistic check. If the column order does not matter to you, just ignore its results.
