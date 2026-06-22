# Check for objects with a name of maximum length

## Why you should keep an eye on the length of database object names

The maximum size of an identifier is 63 bytes.
If it is exceeded, PostgreSQL silently truncates the too-long name.
This can lead to a situation where 2 different objects become identical by name.
For example, if a migration is created in which indexes with long names are created
that start the same way, then when using the `IF NOT EXISTS` clause only one object may be created instead of several.

## SQL query

- [possible_object_name_overflow.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/possible_object_name_overflow.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed both on the partitioned (parent) table itself and on the individual partitions (children).

## Reproduction script

```sql
create schema if not exists demo;

-- An identifier in PostgreSQL cannot be longer than 63 bytes (max_identifier_length).
-- A longer name is silently truncated to 63 characters, so objects with a name of exactly 63 characters are potentially dangerous.

-- For ordinary (non-partitioned) tables

create table if not exists demo.accounts(
    id bigint primary key generated always as identity,
    client_id bigint not null,
    account_number varchar(50) not null,
    account_balance numeric(22, 2) not null default 0
);

-- A constraint name of exactly 63 characters
alter table if exists demo.accounts
    add constraint num_less_than_million_constraint_with_length_63_1234567890_1234
    check (account_balance < 1000000);

-- A materialized view name of exactly 63 characters
create materialized view if not exists
    demo."accounts-materialized-view-with-length-63-1234567890-1234567890" as (
    select client_id, account_number from demo.accounts);

-- For partitioned tables
-- The partitioned table name is exactly 63 characters long; the automatically created
-- sequence, primary key, and indexes also get names of about 63 characters.

create table if not exists demo.entity_long_1234567890_1234567890_1234567890_1234567890_1234567(
    ref_type  varchar(32),
    ref_value varchar(64),
    entity_id bigserial primary key
) partition by range (entity_id);

create index if not exists idx_entity_long_1234567890_1234567890_1234567890_1234567890_123
    on demo.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 (ref_type, ref_value);

create table if not exists demo.entity_default_long_1234567890_1234567890_1234567890_1234567890
    partition of demo.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 default;
```

## How to fix

Use shorter names for database objects — so that their length is guaranteed to stay below 63 bytes (preferably with some margin).
Pay special attention to names that are generated from a template and, because of a common prefix, may turn out to be
non-unique after truncation (for example, long index and constraint names that start the same way).

If an object with a maximum-length name has already been created, rename it with the `alter ... rename to` commands,
after making sure that the truncation has not caused a collision with another object:

```sql
alter table demo.accounts
    rename constraint num_less_than_million_constraint_with_length_63_1234567890_1234 to balance_less_than_million_chk;

alter materialized view demo."accounts-materialized-view-with-length-63-1234567890-1234567890"
    rename to accounts_mv;
```

Keep in mind that for automatically generated names (primary keys, sequences of `serial` columns, indexes)
PostgreSQL may also exceed 63 bytes and truncate them — take this into account when choosing the name of the table itself and its columns.
