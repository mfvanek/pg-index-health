# Check for constraints that have not been validated

## Why data may not conform to existing constraints

- [Source](https://habr.com/ru/articles/800121/)

Some types of constraints (currently the `CHECK` constraint and, with some reservations, `FOREIGN KEY`)
can be created with the `NOT VALID` clause.
When creating constraints on large tables, checking the already existing data can take a long time,
so developers use a convenient PostgreSQL mechanism and separate the process of creating a constraint from the process of validating all the data.

It is important to note that immediately after creation the constraint takes effect when data is added or modified,
and any of these operations will be aborted if the new data does not conform to the constraint.
However, at the database level a flag is set indicating that the constraint has not been checked against all the data,
until it is validated with the `VALIDATE CONSTRAINT` command.

## SQL query

- [not_valid_constraints.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/not_valid_constraints.sql)

## Check type

- **runtime** (makes sense to run on a live database instance after applying migrations)
- **static** (can be performed in component/integration tests to verify the correctness of migrations)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

-- For ordinary (non-partitioned) tables

create table if not exists demo.clients(
    id bigint primary key generated always as identity,
    first_name varchar(255) not null,
    last_name varchar(255) not null
);

create table if not exists demo.accounts(
    id bigint primary key generated always as identity,
    client_id bigint not null,
    account_number varchar(50) not null,
    account_balance numeric(22, 2) not null default 0
);

-- A foreign key created with the NOT VALID clause: existing rows are not checked
alter table if exists demo.accounts
    add constraint c_accounts_fk_client_id_not_validated_yet
    foreign key (client_id) references demo.clients (id) not valid;

-- A check constraint created with the NOT VALID clause: existing rows are not checked
alter table if exists demo.accounts
    add constraint c_accounts_chk_client_id_not_validated_yet
    check (client_id > 0) not valid;

-- For partitioned tables

create table if not exists demo.orders_partitioned(
    id         bigint      not null generated always as identity,
    user_id    bigint      not null,
    status     int         not null,
    created_at timestamptz not null default current_timestamp,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.orders_default
    partition of demo.orders_partitioned default;

-- A check constraint with the NOT VALID clause on the partitioned (parent) table itself
alter table if exists demo.orders_partitioned
    add constraint c_orders_chk_status_not_validated_yet
    check (status >= 0) not valid;
```

## How to fix

To bring a constraint into a valid state, run the [`VALIDATE CONSTRAINT`](https://www.postgresql.org/docs/17/sql-altertable.html) command.
It checks all the rows already present in the table for conformance to the constraint but, unlike creating the constraint,
it does not block reads and writes — only a `SHARE UPDATE EXCLUSIVE` lock is taken.

For ordinary tables:

```sql
alter table demo.accounts validate constraint c_accounts_fk_client_id_not_validated_yet;
alter table demo.accounts validate constraint c_accounts_chk_client_id_not_validated_yet;
```

For partitioned tables, validate the constraint on the partitioned (parent) table itself:

```sql
alter table demo.orders_partitioned validate constraint c_orders_chk_status_not_validated_yet;
```

If the `VALIDATE CONSTRAINT` command fails, it means the table contains data that does not conform to the constraint.
Find and fix such rows (or drop the constraint itself if it is incorrect), and then repeat the validation.
