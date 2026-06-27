# Check for columns with the char(n) type in tables

Using the `char(n)` or `character(n)` type is most likely a mistake and should almost always be replaced with the `text` type.

## Why you should not use it

Any string that you insert into a `char(n)` field will be padded with spaces to the declared width.
Padding a value in a column with spaces leads to wasted space, but does not speed up operations with that value.

Additional sources:
- [Character Types ](https://www.postgresql.org/docs/current/datatype-character.html)
- [Don't use char(n)](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_char(n))
- [Squawk ban-char-field](https://squawkhq.com/docs/ban-char-field)

## SQL query

- [columns_with_char_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_char_type.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create table if not exists demo.orders
(
    id bigint primary key generated always as identity,
    user_id bigint not null,
    shop_id bigint not null,
    status int not null,
    created_at timestamptz not null default current_timestamp,
    user_sex char not null,
    user_name char(100) not null,
    user_surname character(200) not null ,
    user_patronymic bpchar(100),
    user_second_name bpchar
);

create table if not exists demo.orders_partitioned
(
    id         bigint not null generated always as identity,
    user_id    bigint      not null,
    shop_id    bigint      not null,
    status     int         not null,
    created_at timestamptz not null default current_timestamp,
    user_sex char not null,
    user_name char(100) not null,
    user_surname character(200) not null ,
    user_patronymic bpchar(100),
    user_second_name bpchar,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.orders_default
    partition of demo.orders_partitioned default;
```

## How to fix

Change the column type to `text`. Do not forget to make the necessary changes in the application working with the database.
