# Check for objects whose names do not follow the naming convention

The check finds names of database objects that need to be escaped with double quotes in SQL queries.

## Why you should pay attention to this

- [Naming convention](https://www.postgresql.org/docs/17/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS)

You should avoid identifiers that require wrapping in double quotes.
This is inconvenient and can lead to [non-obvious errors](https://lerner.co.il/2013/11/30/quoting-postgresql/).  
See also [wiki.postgresql.org](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_upper_case_table_or_column_names).

## How can such identifiers appear?

If you use Liquibase and the XML syntax, you can easily end up with incorrect names of database objects:

```xml
<changeSet author="author" id="add-index-on-task-expiration-date-status-v2">
    <createIndex tableName="my_task" indexName="idx-my-task-expired-at-status">
        <column name="task_expired_at"/>
        <column name="task_status"/>
    </createIndex>
</changeSet>
```

The problem here is the index name `idx-my-task-expired-at-status`.
Hyphens `-` were used instead of underscores `_`.
Such a migration will be applied without errors, but from now on any reference to this index from SQL will require double quotes:

```sql
reindex index concurrently "idx-my-task-expired-at-status";
```

## SQL query

- [objects_not_following_naming_convention.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/objects_not_following_naming_convention.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed both on the partitioned (parent) table itself and on the individual partitions (children).

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

alter table if exists "bad-demo"."bad-table-two"
    add constraint "bad-table-two-fk-bad-ref-id" foreign key ("bad-ref-id") references "bad-demo"."bad-table" ("bad-id");

create table if not exists "bad-demo"."one-partitioned"(
    "bad-id" bigserial not null primary key
) partition by range ("bad-id");

create table if not exists "bad-demo"."one-default"
    partition of "bad-demo"."one-partitioned" default;

create or replace function "bad-demo"."bad-add"(a integer, b integer) returns integer
as
'select $1 + $2;'
    language sql
    immutable
    returns null on null input;
```

## How to fix

Carefully rename the database objects and bring their names into line with the naming convention —
use only lowercase letters, digits, and underscores `_`, so that the names no longer require wrapping in double quotes.

Different types of objects use different `alter` commands:

```sql
-- tables
alter table "bad-demo"."bad-table" rename to bad_table;

-- constraints
alter table "bad-demo"."bad-table-two"
    rename constraint "bad-table-two-fk-bad-ref-id" to bad_table_two_fk_bad_ref_id;

-- partitioned tables and their partitions
alter table "bad-demo"."one-partitioned" rename to one_partitioned;
alter table "bad-demo"."one-default" rename to one_default;

-- functions
alter function "bad-demo"."bad-add"(integer, integer) rename to bad_add;
```

The rename itself is a fast metadata-only operation, but keep in mind that changing an object's name
breaks all references to it in the application code, in migrations, and in other database objects (views, functions, triggers).
For this reason, renaming must be coordinated with the corresponding code changes and performed in a separate migration.

If your database runs online and downtime is not acceptable, note that renaming a table briefly
takes an `ACCESS EXCLUSIVE` lock. The best way to avoid such problems entirely is to give objects correct names right when they are created.
