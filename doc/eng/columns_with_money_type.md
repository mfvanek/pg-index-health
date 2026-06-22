# Check for columns with the money type in tables

## Why you should not use it

The PostgreSQL developers [do not recommend](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_money) using the `money` type at the present time.

The `money` type does not handle fractions of a cent (or equivalents in other currencies).
That is, it is not suitable for storing exchange rates and performing operations related to currency conversion.

It does not store the currency together with the value; instead, it assumes that all `money` columns contain the currency specified in the `lc_monetary` parameter for the database.
If for some reason you change the `lc_monetary` parameter, then all `money` columns will essentially contain an incorrect value.
This means that if you enter "$10.00" while `lc_monetary` is set to "en_US.UTF-8",
then the resulting value may become "10.00 Lei" or "¥1000" if the `lc_monetary` parameter is changed.

To store monetary values, it is better to use the `numeric` type (with the required precision specified)
and to specify the [ISO code](https://en.wikipedia.org/wiki/ISO_4217) of the currency used in an adjacent column.

## SQL query

- [columns_with_money_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_money_type.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo."bad-money"
(
    id bigint generated always as identity primary key,
    "amount-bad" money not null,
    amount numeric(22, 2) null,
    currency_code text not null
);

create table if not exists demo."bad-money_partitioned"
(
    id uuid not null primary key,
    "amount-bad" money not null,
    amount numeric(22, 2) null,
    currency_code text not null
) partition by hash (id);

create table if not exists demo."bad_money_partitioned_hash_p0"
    partition of demo."bad-money_partitioned"
        for values with (modulus 4, remainder 0);
```

## How to fix

Change the type of the target column to `numeric` and add a separate column of type `text` to store the currency code.
Do not forget to make the necessary changes in the application working with the database.
