# Check for b-tree indexes on columns containing an array of values

## How a b-tree index works on columns with an array of values

A b-tree index on such columns is efficient if you need to compare arrays as a whole,
since it [works with equality conditions](https://www.postgresql.org/docs/17/gin.html).
If you need to check whether elements are contained in the array, it is no longer suitable.

## Why a GIN index is a better fit

A GIN index is implemented as a B-tree built on keys — the elements of the array, [see the documentation for details](https://www.postgresql.org/docs/17/gin.html).
Therefore, it is suitable when you need to compare elements of an array in columns of the array type.

## SQL query

- [btree_indexes_on_array_columns.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/btree_indexes_on_array_columns.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo."table_with_b-tree_index_on_array"(
    id bigint not null,
    login text,
    roles text[]
);

create index if not exists roles_btree_idx
    on demo."table_with_b-tree_index_on_array"(roles) where roles is not null;
    
create index if not exists login_roles_btree_idx
    on demo."table_with_b-tree_index_on_array"(login, roles);

create table if not exists demo."table_with_b-tree_index_on_array_partitioned"(
    id bigint not null,
    login text,
    roles text[]
) partition by hash (login);

create index if not exists roles_btree_partitioned_idx
    on demo."table_with_b-tree_index_on_array_partitioned"(roles) where roles is not null;

create index if not exists login_roles_btree_partitioned_idx
    on demo."table_with_b-tree_index_on_array_partitioned"(login, roles);

create table if not exists demo."table_with_b-tree_index_on_array_hash_p0"
    partition of demo."table_with_b-tree_index_on_array_partitioned"
    for values with (modulus 4, remainder 0);
```

## How to fix

Consider using a GIN index.
