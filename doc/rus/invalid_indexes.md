# Проверка наличия невалидных индексов в таблицах

## Почему может появиться невалидный индекс

Иногда команда создания индекса CREATE INDEX CONCURRENTLY [завершается ошибкой](https://postgrespro.ru/docs/postgresql/17/sql-createindex).
В этом случае индекс создается, но остаётся в невалидном состоянии и игнорируется при чтении данных.

## Почему нужно избавляться от невалидных индексов

При вставке\удалении\обновлении данных невалидный индекс может использовать ресурсы системы так же, как и валидный, хотя он бесполезен.
Если такой индекс уникальный (при этом он может быть составным, индексом по выражениям или частичным),
то ограничение уникальности может остаться и влиять на изменения данных.
Так же невалидный индекс занимает место на диске.

## SQL запрос

- [invalid_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/invalid_indexes.sql)

## Тип проверки

- **runtime** (имеет смысл запускать на работающем инстансе БД после выполнения миграций)
- **static** (может выполняться в компонентных\интеграционных тестах при наличии миграций с проливкой начальных данных)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы. Проверка выполняется на каждой секции.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.table_with_invalid_indexes(
    id bigint not null primary key,
    first_name text,
    last_name text,
    phone varchar(15)
);

insert into demo.table_with_invalid_indexes (id, first_name, last_name, phone)
    values (1, 'Mary', 'Jones', '+12345678910'),
           (2, 'Mary', 'Jones', '+12345678910');

create unique index concurrently if not exists i_first_last_name
    on demo.table_with_invalid_indexes (first_name, last_name);

create table if not exists demo.table_with_invalid_indexes_partitioned(
    id bigint not null primary key,
    first_name text,
    last_name text,
    phone varchar(15)
) partition by range (id);

create table if not exists demo.table_with_invalid_indexes_partitioned_hash_1_100
    partition of demo.table_with_invalid_indexes_partitioned
    for values from (1) to (10);

insert into demo.table_with_invalid_indexes_partitioned (id, first_name, last_name, phone)
values (1, 'Mary', 'Jones', '+12345678910'),
       (2, 'Mary', 'Jones', '+12345678910');

create unique index concurrently if not exists i_first_last_name_p
    on demo.table_with_invalid_indexes_partitioned_hash_1_100 (first_name, last_name);
```
