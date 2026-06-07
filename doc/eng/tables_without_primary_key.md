# Check for tables without primary keys

## Specifics of creating tables

PostgreSQL allows creating tables without a primary key, but such a design can lead to future problems
related to table maintenance.
For example, [pg_repack](https://github.com/reorg/pg_repack) cannot process tables without a primary key or another unique constraint.
The situation is similar with [logical replication](https://www.postgresql.org/docs/current/logical-replication-publication.html) -
replicated tables require a primary key or another unique constraint to work efficiently.

## SQL query

- [tables_without_primary_key.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_primary_key.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed both on the partitioned table itself (the parent one) and on each partition.

## Reproduction script

```sql
create schema if not exists demo;

-- For ordinary (non-partitioned) tables

-- A table without a primary key
create table if not exists demo.bad_clients (
    id bigint not null,
    name varchar(255) not null,
    real_client_id integer,
    email varchar(200),
    phone varchar(51)
);

-- For partitioned tables

-- A partitioned table without a primary key.
-- The check will find both the parent table itself and each of its partitions.
create table if not exists demo.entity_reference(
    ref_type varchar(32) not null,
    ref_value varchar(64) not null,
    creation_date timestamptz not null,
    entity_id varchar(64) not null
) partition by range (creation_date);

create table if not exists demo.entity_reference_default
    partition of demo.entity_reference default;
```

## How to fix

Add a primary key to the table.

If the table already has a column (or a set of columns) that uniquely identifies a row, use it:

```sql
alter table demo.bad_clients
    add primary key (id);
```

If there is no natural key, add a surrogate key based on an auto-incrementing column:

```sql
alter table demo.bad_clients
    add column new_id bigint generated always as identity primary key;
```

For partitioned tables, the primary key must include all partitioning columns.
Create it on the parent table — it will automatically propagate to all partitions:

```sql
alter table demo.entity_reference
    add primary key (ref_type, ref_value, creation_date, entity_id);
```
