# Check for indexes on boolean columns in tables

## Why such indexes are usually not needed

A boolean column can take only two values: true and false. Therefore, such an index has low cardinality.
Indexes on columns with low cardinality are rarely used by the query planner, since they speed up data search only insignificantly. The planner will most likely choose a full scan when filtering by a boolean column, if the table is small or if the query requires a large percentage of the rows.

## How to better organize an index that includes a search by a boolean column

If the rows with a particular value of a boolean column are much fewer than half, and such rows are more often the target of the search,
then [you can create a partial index](https://www.postgresql.org/docs/17/indexes-partial.html).

## SQL query

- [indexes_with_boolean.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_boolean.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create sequence if not exists demo.accounts_seq;

create table if not exists demo.accounts (
    id bigint not null primary key default nextval('demo.accounts_seq'),
    client_id bigint not null,
    account_number varchar(50) not null unique,
    account_balance numeric(22,2) not null default 0,
    deleted boolean not null default false
);

create index if not exists i_accounts_deleted
    on demo.accounts (deleted);

create unique index if not exists i_accounts_account_number_deleted
    on demo.accounts (account_number, deleted);

create table if not exists demo.dict(
    ref_type int not null primary key,
    description text
);

create table if not exists demo.partitioned_table(
    ref_value varchar(64) not null,
    ref_type bigserial not null references demo.dict(ref_type),
    creation_date timestamp with time zone not null,
    entity_id varchar(64) not null,
    deleted boolean not null,
    primary key (ref_value, ref_type, creation_date, entity_id)
) partition by range (creation_date);

create index if not exists idx_t1_deleted on demo.partitioned_table(deleted);

create table if not exists demo.t1_default
    partition of demo.partitioned_table default;
```

## How to fix

Consider creating and using partial indexes with a predicate on the boolean field.
