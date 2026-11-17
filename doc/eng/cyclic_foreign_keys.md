# Check for cyclic foreign keys between tables

## What they are and why you should avoid them

A cycle of foreign keys is a chain of foreign keys that leads from a table back to itself through one or more other tables.
The simplest example: `orders.customer_id` references `customers`, and `customers.last_order_id` references `orders`.

Such cyclical relationships are considered a design smell:

- Inserts and deletes need a specific order of statements followed by an update, or deferred constraints.
- `pg_dump --data-only` warns about circular foreign keys, and such a dump may not be restorable without `--disable-triggers`.
- `truncate` works only for all tables of the cycle at once (or with `cascade`).
- Fixtures and ORMs cannot derive an order for loading the data.

The check reports **any** cycle, even if its foreign keys are `deferrable` or built on nullable columns.
Each cycle is reported exactly once as an ordered chain of foreign keys:
every foreign key references the table of the next foreign key in the chain, and the last one references the first table.
The cycle starts from the table with the smallest name; the first table has no special meaning.

Cycles are enumerated over constraints rather than over pairs of tables:
two parallel foreign keys between the same tables produce two separate cycles, because both must be dealt with.

The check does not report:

- self-referenced foreign keys (a table that references itself);
- cycles that pass through a table in another schema.

Foreign keys declared as `not enforced` (PostgreSQL 18+) are treated as regular foreign keys.

The number of simple cycles can grow exponentially in a densely connected graph,
so the check may run for a long time on a large schema with a lot of intertwined cycles.

If a cycle is an accepted design decision, exclude it by the name of its tables (`SkipTablesByNamePredicate`)
or by the name of its constraints (`SkipByConstraintNamePredicate`).

See also the [LinterTableCycles](https://www.schemacrawler.com/lint.html) linter in SchemaCrawler.

## SQL query

- [cyclic_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/cyclic_foreign_keys.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored:
a foreign key declared directly on a partition or referencing a partition directly does not take part in cycles.

## Reproduction script

```sql
create schema if not exists demo;

-- For ordinary (non-partitioned) tables

create table if not exists demo.customers(
    id bigint primary key generated always as identity,
    name text not null,
    last_order_id bigint
);

create table if not exists demo.orders(
    id bigint primary key generated always as identity,
    customer_id bigint not null,
    created_at timestamptz not null default current_timestamp,
    constraint orders_customer_fk foreign key (customer_id) references demo.customers (id)
);

alter table if exists demo.customers
    add constraint customers_last_order_fk foreign key (last_order_id) references demo.orders (id);

-- For partitioned tables

create table if not exists demo.customers_partitioned(
    id bigint not null primary key,
    name text not null,
    last_order_id bigint
) partition by range (id);

create table if not exists demo.customers_default
    partition of demo.customers_partitioned default;

create table if not exists demo.orders_for_partitioned(
    id bigint primary key generated always as identity,
    customer_id bigint not null,
    created_at timestamptz not null default current_timestamp,
    constraint orders_for_partitioned_customer_fk foreign key (customer_id) references demo.customers_partitioned (id)
);

alter table if exists demo.customers_partitioned
    add constraint customers_partitioned_last_order_fk foreign key (last_order_id) references demo.orders_for_partitioned (id);
```

## How to fix

Only a change of the data model removes a cycle, so there is no universal migration:

- Drop the back-reference and calculate the value with a query instead
  (for example, find the last order of a customer with `order by created_at desc limit 1` rather than store `last_order_id`).
- Move the relationship into a separate link table that references both tables.

A reasonable hint for choosing the constraint to remove is the foreign key that takes part in the largest number of reported cycles.

Making a foreign key `deferrable` or its columns nullable makes the cycle usable, but it does not remove it: the check will still report it.
