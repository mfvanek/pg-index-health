# Check for composite (multi-column) foreign keys with nullable columns that are not defined with `MATCH FULL`

If a foreign key spans multiple columns (a composite foreign key) and some of those columns are nullable,
it becomes possible to insert rows into the referencing table that do not exist in the referenced table.

By default, PostgreSQL creates foreign keys using the `MATCH SIMPLE` option.
This allows any column in the foreign key to be `NULL`.
If at least one column is `NULL`, PostgreSQL does not require a matching row in the referenced table.

The `MATCH FULL` option changes this behavior.
It requires that either all columns of the foreign key are `NULL`, or none of them are.
In other words, partial `NULL` values are not allowed.

See details in the [official documentation](https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK)
and [this Habr article](https://habr.com/ru/articles/803841/).

## SQL query

- [foreign_keys_with_null_values.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_with_null_values.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table demo.referenced_table
(
    id    int not null,
    value text not null,
    primary key (id, value)
);

-- Filling with data
insert into demo.referenced_table (id, value) values (10, '10');
insert into demo.referenced_table (id, value) values (20, '20');

create table demo.referencing_bad_table
(
    rbt_id integer not null,
    rbt_value text, -- nullable
    constraint "referencing-bad-table-fk" foreign key (rbt_id, rbt_value)
        references demo.referenced_table (id, value)
);

-- Since the rbt_value field can contain null, both records will be added to the table.
-- If the constraint referencing_bad_table_fk had been set to MATCH FULL, the second entry would not have been added.
-- https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK
insert into demo.referencing_bad_table (rbt_id, rbt_value) values (20, '20');
insert into demo.referencing_bad_table (rbt_id, rbt_value) values (30, null);

table demo.referencing_bad_table;

create table demo.referencing_good_table
(
    rgt_id integer not null,
    rgt_value text, -- nullable
    constraint referencing_good_table_fk foreign key (rgt_id, rgt_value)
        references demo.referenced_table (id, value) match full
);

insert into demo.referencing_good_table (rgt_id, rgt_value) values (20, '20');
-- insert into demo.referencing_good_table (rgt_id, rgt_value) values (30, null); -- error!

table demo.referencing_good_table;

-- For partitioned tables

create table if not exists demo.referencing_bad_table_partitioned(
    rbt_id integer not null,
    rbt_value text, -- nullable
    created_at timestamptz not null default current_timestamp,
    constraint "referencing_bad_table_partitioned-fk" foreign key (rbt_id, rbt_value)
        references demo.referenced_table (id, value)
) partition by range (created_at);

create table if not exists demo.referencing_bad_table_default
    partition of demo.referencing_bad_table_partitioned default;

insert into demo.referencing_bad_table_partitioned (rbt_id, rbt_value) values (20, '20');
insert into demo.referencing_bad_table_partitioned (rbt_id, rbt_value) values (30, null);

table demo.referencing_bad_table_partitioned;
```

## How to fix

For composite foreign keys containing nullable columns, use the `MATCH FULL` option.
