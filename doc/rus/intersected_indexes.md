# Проверка наличия пересекающихся индексов в таблицах

## Как появляется пересечение индексов

Составной индекс может применяться в условиях [с любым подмножеством столбцов индекса](https://postgrespro.ru/docs/postgresql/17/indexes-multicolumn).
Например, индекс для столбцов А+В так же эффективен при поиске по столбцу А, как и индекс только по столбцу А.
Нужно только учесть, что в случае составного btree-индекса поиск будет эффективен, когда в условие входят ведущие (левые) столбцы составного индекса.

## Почему нужно избавляться от пересекающихся индексов

При изменении данных обновляется [каждый индекс](https://postgrespro.ru/docs/postgresql/17/btree),
таким образом дубликаты снижают производительность.

## SQL запрос

- [intersected_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/intersected_indexes.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."table_with_intersected_indexes"
(
    id bigint not null primary key,
    first_name text,
    last_name text,
    phone varchar(15)
);

create index concurrently if not exists i_first_last_name on demo."table_with_intersected_indexes" (first_name, last_name);

create index concurrently if not exists i_first_last_name_phone on demo."table_with_intersected_indexes" (first_name, last_name, phone);

create table if not exists demo."table_with_intersected_indexes_partitioned"
(
    id bigint not null primary key,
    first_name text,
    last_name text,
    phone varchar(15)
) partition by hash (name);


create index if not exists i_first_last_name_p on demo."table_with_intersected_indexes_partitioned" (first_name, last_name);

create index if not exists i_first_last_name_phone_p on demo."table_with_intersected_indexes_partitioned" (first_name, last_name, phone);

create table if not exists demo."table_with_intersected_indexes_partitioned_hash_p0"
    partition of demo."table_with_intersected_indexes_partitioned"
    for values with (modulus 4, remainder 0);
```
