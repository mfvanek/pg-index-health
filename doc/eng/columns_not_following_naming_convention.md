# Check for columns whose names do not follow the naming convention

The check finds names of columns in database tables that need to be escaped with double quotes in SQL queries.

## Why you should pay attention to this

- [Naming convention](https://www.postgresql.org/docs/17/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS)

You should avoid column names that require wrapping in double quotes.
This is inconvenient and can lead to [non-obvious errors](https://lerner.co.il/2013/11/30/quoting-postgresql/).  
See also [wiki.postgresql.org](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_upper_case_table_or_column_names).

## SQL query

- [columns_not_following_naming_convention.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_not_following_naming_convention.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists "bad-demo";

create table if not exists "bad-demo"."bad-table"(
    "bad-id" serial not null primary key
);

create table if not exists "bad-demo"."bad-table-two"(
    "bad-ref-id" int not null primary key,
    description  text
);

create table if not exists "bad-demo"."one-partitioned"(
    "bad-id" bigserial not null primary key
) partition by range ("bad-id");

create table if not exists "bad-demo"."one-default"
    partition of "bad-demo"."one-partitioned" default;
```

## How to fix

Carefully rename the columns and bring their names into line with the naming convention.  
If your database runs online and downtime is not acceptable,
then instead of renaming a column use the [approach of creating a new column](https://habr.com/ru/companies/karuna/articles/568240/)
and gradually switching over to using it.
