# Check for functions without a description

## Why you should add a description to functions

With a description, it is easier to apply a function correctly and to find possible errors in its definition.
Having a description for functions/procedures makes them easier to maintain and modify in the future.

## SQL query

- [functions_without_description.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/functions_without_description.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Not applicable to partitioned tables.

## Reproduction script

```sql
create schema if not exists demo;

create or replace function demo.add(a integer, b integer) returns integer
as 'select $1 + $2;'
    language sql
    immutable
    returns null on null input;

create or replace function demo.add(a int, b int, c int) returns int
as 'select $1 + $2 + $3;'
    language sql
    immutable
    returns null on null input;
```

## How to fix

Add human-readable descriptions to all functions.
