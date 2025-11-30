# Проверка b-tree индексов на столбцах, содержащих массив значений

## Как работает b-tree индекс на столбцах с массивом значений

B-tree индекс на таких столбцах эффективен, если нужно сравнивать массивы целиком,
так как он [работает в условиях на равенство](https://postgrespro.ru/docs/postgresql/17/gin).
Если нужно проверять вхождение элементов в массив, то он уже не подойдет.

## Почему больше подойдет индекс типа GIN

Индекс GIN реализован как B-дерево, построенное по ключам — элементам массива, [подробнее в документации](https://postgrespro.ru/docs/postgresql/17/gin).
Поэтому он подойдет, если потребуется сравнивать элементы массива в столбцах типа array.

## SQL запрос

- [btree_indexes_on_array_columns.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/btree_indexes_on_array_columns.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."table_with_b-tree_index_on_array"
(
    id bigint not null,
    login text,
    roles text[]
);

create index if not exists roles_btree_idx
    on demo."table_with_b-tree_index_on_array"(roles) where roles is not null;
    
create index if not exists login_roles_btree_idx
    on demo."table_with_b-tree_index_on_array"(login, roles);

create table if not exists demo."table_with_b-tree_index_on_array_partitioned"
(
    id bigint not null,
    login text,
    roles text[]
) partition by hash (login);

create index if not exists roles_btree_idx
    on demo."table_with_b-tree_index_on_array_partitioned"(roles) where roles is not null;
    
create index if not exists login_roles_btree_idx
    on demo."table_with_b-tree_index_on_array_partitioned"(login, roles);

create table if not exists demo."table_with_b-tree_index_on_array_hash_p0"
    partition of demo."table_with_b-tree_index_on_array_partitioned"
    for values with (modulus 4, remainder 0);
```
