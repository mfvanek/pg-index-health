# Check sequences for overflow

## Why you should check the state of a sequence

Surrogate primary keys are often populated from a sequence.
If there is a lot of data, the sequence can overflow.
If you do not track the volume of remaining values, fixing the error can lead to a long lock on this table and others,
if the overflowing primary key is a foreign key for them.

## SQL query

- [sequence_overflow.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/sequence_overflow.sql)

## Check type

- **runtime** (makes sense to run on a working database instance)

## Support for partitioned tables

Not applicable to partitioned tables.

## Reproduction script

```sql
create schema if not exists demo;

create sequence demo.seq_1 as smallint increment by 1 maxvalue 100 start 92;

create sequence demo.seq_3 as integer increment by 2 maxvalue 100 start 92;

create sequence demo.seq_5 as bigint increment by 10 maxvalue 100 start 92;

create sequence demo.seq_cycle as bigint increment by 10 maxvalue 100 start 92 cycle;
```

## How to fix

In advance (before the actual overflow), switch to a wider integer type for the sequence
and the column associated with it: `smallint` → `integer` → `bigint`.

If the sequence has an artificially low `maxvalue`, increase it:

```sql
alter sequence demo.seq_3 maxvalue 2147483647;
```

More often, the bottleneck is the type of the primary key column. Widen it to `bigint`:

```sql
alter table demo.some_table
    alter column id type bigint;
```

The sequence type must also be widened in this case:

```sql
alter sequence demo.some_table_id_seq as bigint;
```

Change the column type ahead of time: on large tables `alter column ... type` rewrites the table under a lock
and can take a long time, and it also affects tables that reference this primary key with foreign keys.
