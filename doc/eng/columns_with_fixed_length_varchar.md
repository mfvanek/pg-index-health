# Check for columns with the varchar(n) type in tables

## Recommendation

The [documentation](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_varchar.28n.29_by_default) states that you should not use the `varchar(n)` type by default.
Instead, you should use `varchar` (without a length limit) or `text`.

## Why you should not use it

`varchar(n)` is a variable-length text field that will throw an error
if you try to insert a string longer than **n** characters into it.
`varchar (without (n))` or `text` are similar, but without a length limit.
If you insert the same string into all three field types, they will occupy the same amount of space,
and you will not be able to measure any difference in performance.
If you need to limit the value in a field, you probably need something more specific than a maximum length.

## When to use it

If you need a text field that will throw an error if you insert a string that is too long, and you do not want to use an explicit check constraint, then varchar(n) is a perfectly suitable type.

## SQL query

- [columns_with_fixed_length_varchar.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_fixed_length_varchar.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo."bad_varchar_limit"
(
    id int not null primary key,
    name varchar(20) -- Limits future flexibility
);

create table if not exists demo."bad_varchar_limit_partitioned"
(
    id int not null,
    name varchar(20), -- Limits future flexibility
    primary key (id, name)
) partition by hash (name);

create table if not exists demo."bad_varchar_limit_partitioned_hash_p0"
    partition of demo."bad_varchar_limit_partitioned"
    for values with (modulus 4, remainder 0);
```

## How to fix

Change the column type to `text`. Do not forget to make the necessary changes in the application working with the database.
