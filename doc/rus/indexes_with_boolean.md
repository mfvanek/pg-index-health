# Проверка индексов на boolean-столбцы в таблицах

## Почему такие индексы обычно не нужны

boolean  столбец может принимать только два значения: true и  false. Поэтому у такого индекса низкая кардинальность.
Индексы на столбцах с низкой кардинальностью редко используются планировщиком запросов, так как они незначительно ускоряют поиск данных. Планировщик, скорее всего, выберет полное сканирование при фильтрации по boolean-столбцу, если таблица небольшая или если запрос требует большого процента строк.

## Как лучше организовать индекс, включащий поиск по boolean-столбцу

Если строк с определенным значением столбца типа boolean гораздо меньше половины и при поиске чаще ищутся именно такие строки,
то [можно создать частичный индекс](https://postgrespro.ru/docs/postgrespro/17/indexes-partial)

## SQL запрос

- [indexes_with_boolean.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_boolean.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."table_with_boolean_indexed"
(
    id bigint not null primary key,
    first_name text,
    last_name text,
    account_number varchar(64),
    created timestamp with time zone not null,
    is_debtor boolean
);

create index if not exists i_is_debtor on demo."table_with_boolean_indexed" (is_debtor);

create table if not exists demo."table_with_boolean_indexed_partitioned"
(
    id integer not null primary key,
    first_name text,
    last_name text,
    account_number varchar(64),
    created timestamp with time zone not null,
    is_debtor boolean
) partition by range (created);

create index if not exists i_is_debtor_part on demo."table_with_boolean_indexed_partitioned" (is_debtor);

create table if not exists demo."table_with_boolean_indexed_partitioned_2024"
    partition of demo."duplicated_indexes_partitioned"
    for values from ('2024-01-01') to ('2024-12-31');
```
