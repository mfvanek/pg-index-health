# Check for columns with the json type in tables

## How to store json in a table

To store data in json format, PostgreSQL has two types: json and jsonb.
The first type stores an exact copy of the recorded text, the second one stores json data in binary format.

## Why the jsonb type is better

The documentation [states directly](https://www.postgresql.org/docs/17/datatype-json.html) that **jsonb** is preferable.
**jsonb** supports constraints and indexes, which makes it possible to search such data faster.
Only inserting data into the jsonb type is slower than into json, because the data needs to be converted into binary format.

**jsonb** and json differ when comparing objects of each type. jsonb does not preserve the order of keys when storing. If the input data has duplicate keys, only the last value is kept.
For the json type, objects with a different order of the same key-value pairs are considered different. The order of the keys is preserved as in the input data, and all key-value pairs with the same key are preserved.
The ordinary [comparison operators](https://www.postgresql.org/docs/17/functions-comparison.html#FUNCTIONS-COMPARISON-OP-TABLE) are applicable only to jsonb.
For data in jsonb format, you can use the DISTINCT operator.
For json fields, DISTINCT does not work.
If a query for a json field is generated with such an operator (for example, by an ORM), this will lead to errors.
Thus, if efficient search or comparison is required when working with json-format data, the jsonb type is needed.

## SQL query

- [columns_with_json_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_json_type.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.table_with_json_column(
    ref_type varchar(32),
    ref_value varchar(64),
    creation_date timestamp with time zone not null,
    entity_id varchar(64) not null,
    real_client_id bigint,
    raw_data json
);

create table if not exists demo.table_with_json_column_partitioned(
    ref_type varchar(32),
    ref_value varchar(64),
    creation_date timestamp with time zone not null,
    entity_id varchar(64) not null,
    real_client_id bigint,
    raw_data json
) partition by range (creation_date);

create table if not exists demo.table_with_json_column_partitioned_q3
    partition of demo.table_with_json_column_partitioned
        for values from ('2025-07-01') to ('2025-10-01');
```

## How to fix

Change the column type to `jsonb`. Do not forget to make the necessary changes in the application working with the database.
