# Check for columns of the serial type that are not a primary key

## Features of the serial type in PostgreSQL

The `smallserial`, `serial`, and `bigserial` data types are syntactic sugar.
They are implemented via [sequences of integers](https://www.postgresql.org/docs/17/datatype-numeric.html).

## Why serial should not be used

A sequence is created for the column as a default value, from which subsequent values will be calculated.
Extra objects that are not actually needed are created in the database.
In modern versions of PostgreSQL, [it is better not to use serial types even for primary keys](primary_keys_with_serial_types.md).

## SQL query

- [columns_with_serial_types.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_serial_types.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.table_with_serial_column(
    ref_type smallserial,
    ref_value serial,
    real_client_id bigserial    
);

create table if not exists demo.table_with_serial_column_partitioned(
    ref_type smallserial,
    ref_value serial,
    creation_date timestamp with time zone not null,
    real_client_id bigserial    
) partition by range (creation_date);

create table if not exists demo.table_with_serial_column_partitioned_q3
    partition of demo.table_with_serial_column_partitioned
        for values from ('2025-07-01') to ('2025-10-01');
```

## How to fix

Remove the default value on the column and the sequence associated with this column.

Use the [ColumnWithSerialTypeMigrationGenerator](../../pg-index-health-generator/src/main/java/io/github/mfvanek/pg/generator/ColumnWithSerialTypeMigrationGenerator.java)
class to automatically generate a corrective database migration.
