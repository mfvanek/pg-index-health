# Check for invalid indexes in tables

## How an invalid index can appear

Sometimes the CREATE INDEX CONCURRENTLY command [fails with an error](https://www.postgresql.org/docs/17/sql-createindex.html).
In this case the index is created but remains in an invalid state and is ignored when reading data.

## Why you should get rid of invalid indexes

On insert/delete/update an invalid index may consume system resources just like a valid one, even though it is useless.
If such an index is unique (and it may also be composite, an expression index, or a partial index),
its uniqueness constraint may remain in effect and affect data modifications.
An invalid index also takes up disk space.

## SQL query

- [invalid_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/invalid_indexes.sql)

## Check type

- **runtime** (makes sense to run on a live database instance after applying migrations)
- **static** (can be performed in component/integration tests when migrations include loading of initial data)

## Support for partitioned tables

Partitioned tables are supported. The check is performed on each partition.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.table_with_invalid_indexes(
    id bigint not null primary key,
    first_name text,
    last_name text,
    phone varchar(15)
);

insert into demo.table_with_invalid_indexes (id, first_name, last_name, phone)
    values (1, 'Mary', 'Jones', '+12345678910'),
           (2, 'Mary', 'Jones', '+12345678910');

create unique index concurrently if not exists i_first_last_name
    on demo.table_with_invalid_indexes (first_name, last_name);

create table if not exists demo.table_with_invalid_indexes_partitioned(
    id bigint not null primary key,
    first_name text,
    last_name text,
    phone varchar(15)
) partition by range (id);

create table if not exists demo.table_with_invalid_indexes_partitioned_hash_1_100
    partition of demo.table_with_invalid_indexes_partitioned
    for values from (1) to (10);

insert into demo.table_with_invalid_indexes_partitioned (id, first_name, last_name, phone)
values (1, 'Mary', 'Jones', '+12345678910'),
       (2, 'Mary', 'Jones', '+12345678910');

create unique index concurrently if not exists i_first_last_name_p
    on demo.table_with_invalid_indexes_partitioned_hash_1_100 (first_name, last_name);
```

## How to fix

If you were creating a unique index,
make sure there are no duplicates in the table data that prevent the index from being created.
In other cases, try to rebuild the index using the [reindex concurrently](https://www.postgresql.org/docs/current/sql-reindex.html) command.
