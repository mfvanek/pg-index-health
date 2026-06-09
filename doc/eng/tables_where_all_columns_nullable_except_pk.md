# Check for tables where all columns except the primary key are nullable

## Why look for tables where all columns (except the PK) allow `NULL`?

- Data quality
  * Indicates weak modeling and a lack of constraints
  * Increases the risk of inconsistent and useless records
- Performance
  * Additional storage overhead (NULL bitmaps, empty rows)
  * Indexes and queries on such columns work inefficiently
- Maintenance
  * The code can be overloaded with NULL checks
  * Testing complexity and the risk of errors grow
  * It is hard to impose new constraints and migrate data

## SQL query

- [tables_where_all_columns_nullable_except_pk.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_where_all_columns_nullable_except_pk.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.bad_design (
    product_type text not null,
    description text,
    product_subtype text not null,
    primary key (product_type, product_subtype)
);

create table if not exists demo.no_pk (
    description text,
    product_type text,
    product_subtype text
);

create table if not exists demo.only_pk (
    id bigint generated always as identity primary key
);

create table if not exists demo."good-design"
(
    description text not null,
    id bigint generated always as identity primary key
);

create table if not exists demo."bad-design_partitioned"
(
    description text,
    id uuid not null primary key,
    created_at timestamptz
) partition by hash (id);

create table if not exists demo."bad-design_partitioned_hash_p0"
    partition of demo."bad-design_partitioned" for values with (modulus 4, remainder 0);
```

## How to fix

Analyze the data model and add a `not null` constraint to the columns that are actually mandatory.

```sql
alter table demo.bad_design
    alter column description set not null;
```

If the table already has rows with `NULL` in such a column, you must fill in these values before adding the constraint
(for example, with a meaningful default value).

If it turns out that none of the table's columns are mandatory, this is a sign of weak modeling —
reconsider the table design as a whole.
