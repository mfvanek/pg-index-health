# Проверка присутствия индексов, включающих null значения

## Особенности создания b-tree индексов

По умолчанию Postgres [включает null значения в btree-индексы](https://postgrespro.ru/docs/postgresql/17/indexes-ordering).

## Почему нужно удалять null значения из индексов

Это может существенно уменьшить размер индекса в том случае если null значение встречается часто.
Частичный индекс, из которого исключены null значения будет оптимальнее, потому что при поиске
распространённого значения [индекс всё равно не будет использоваться](https://postgrespro.ru/docs/postgresql/17/indexes-partial).
Поиск будет проходить быстрее. Индекс будет занимать меньше места на диске.

## SQL запрос

- [indexes_with_null_values.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_null_values.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."table_with_null_value_index"
(
    id bigint not null primary key,
    first_name text not null,
    last_name text not null,
    created timestamp with time zone not null,
    updated timestamp with time zone
);

create index concurrently if not exists i_is_debtor on demo."table_with_null_value_index" (updated);

create table if not exists demo."table_with_null_value_index_partitioned"
(
    id bigint not null primary key,
    first_name text not null,
    last_name text not null,
    created timestamp with time zone not null,
    updated timestamp with time zone
) partition by range (created);

create index if not exists i_is_debtor_part on demo."table_with_null_value_index_partitioned" (updated);

create table if not exists demo."table_with_boolean_indexed_partitioned_2024"
    partition of demo."table_with_boolean_indexed_partitioned"
    for values from ('2024-01-01') to ('2024-12-31');
```
