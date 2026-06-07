# Check for unused indexes in tables

## How unused indexes appear

PostgreSQL may never use some indexes.

There can be many reasons for this:
* the presence of similar indexes (duplicated or intersecting)
* a small table size
* low index selectivity, and others.

## Why you should get rid of unused indexes

Every [index is updated](https://www.postgresql.org/docs/current/btree.html) when data changes, so redundant indexes degrade performance.
Each index also takes up space on disk.

## SQL query

- [unused_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unused_indexes.sql)

## Check type

- **runtime** (requires accumulated statistics)

In a database cluster, the check must be performed on every host.

## Support for partitioned tables

Supports partitioned tables. The check is performed on each partition.

## Reproduction script

```sql
create schema if not exists demo;

-- For ordinary (non-partitioned) tables

create table if not exists demo.clients (
    id bigint not null primary key generated always as identity,
    last_name varchar(255) not null,
    first_name varchar(255) not null,
    email varchar(200) not null,
    phone varchar(50) not null
);

create table if not exists demo.accounts (
    id bigint not null primary key generated always as identity,
    client_id bigint not null references demo.clients (id),
    account_number varchar(50) not null unique,
    account_balance numeric(22, 2) not null default 0,
    deleted boolean not null default false
);

-- Indexes that nobody uses
create index if not exists i_clients_last_name
    on demo.clients (last_name);
create index if not exists i_clients_last_first
    on demo.clients (last_name, first_name);
create index if not exists i_accounts_account_number
    on demo.accounts (account_number);
create index if not exists i_accounts_account_number_not_deleted
    on demo.accounts (account_number) where not deleted;
create index if not exists i_accounts_number_balance_not_deleted
    on demo.accounts (account_number, account_balance) where not deleted;

-- Filling the tables with data
insert into demo.clients (last_name, first_name, email, phone)
select
    'last_name_' || g.id,
    'first_name_' || g.id,
    'client_' || g.id || '@example.com',
    '+7900' || lpad(g.id::text, 7, '0')
from generate_series(1, 1000) as g(id);

insert into demo.accounts (client_id, account_number)
select c.id, '40702810' || lpad(c.id::text, 12, '0')
from demo.clients c;

-- Update statistics so that the values in pg_stat_*_indexes become accurate
vacuum analyze demo.clients;
vacuum analyze demo.accounts;

-- For partitioned tables

create table if not exists demo.entity_reference(
    ref_type varchar(32),
    ref_value varchar(64),
    creation_date timestamptz not null,
    entity_id varchar(64) not null
) partition by range (creation_date);

create index if not exists idx_entity_reference_type_value
    on demo.entity_reference (ref_type, ref_value);
create index if not exists idx_entity_reference_entity_value
    on demo.entity_reference (entity_id, ref_value);

create table if not exists demo.entity_reference_default
    partition of demo.entity_reference default;
```

## How to fix

Drop the unused indexes.

```sql
drop index concurrently if exists demo.i_clients_last_name;
```

Use `drop index concurrently` so that writes to the table are not blocked while the index is being dropped.

Before dropping, make sure statistics have been accumulated on all hosts of the cluster over a sufficiently long period
(including rare operations such as reports, nightly jobs, and maintenance), and that enough time has passed since the last
statistics reset (`pg_stat_reset()`).
An index that was not used on the primary host may be actively used on a replica.
