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

Поддерживает секционированные таблицы. Проверка выполняется как на секционированных (родительских) таблицах, так и на каждой секции.

Индекс на секционированной таблице невалиден, пока к нему присоединены индексы не всех секций
(например, если он был создан командой `create index ... on only`).
Индексы на секциях также попадают в результат: невалидный индекс может быть присоединён к секционированному индексу,
и тогда пересоздавать нужно именно индекс на секции.

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

create table if not exists demo.orders_partitioned(
    id bigint not null,
    creation_date timestamp with time zone not null,
    primary key (id, creation_date)
) partition by range (creation_date);

create table if not exists demo.orders_partitioned_2025
    partition of demo.orders_partitioned for values from ('2025-01-01') to ('2026-01-01');

create table if not exists demo.orders_partitioned_default
    partition of demo.orders_partitioned default;

-- индекс на секционированной таблице остаётся невалидным: к нему присоединён индекс только одной из двух секций
create index if not exists idx_orders_partitioned_creation_date on only demo.orders_partitioned (creation_date);
create index if not exists idx_orders_partitioned_2025_creation_date on demo.orders_partitioned_2025 (creation_date);
alter index demo.idx_orders_partitioned_creation_date attach partition demo.idx_orders_partitioned_2025_creation_date;
```

## Как исправить

Если вы создавали уникальный индекс,
убедитесь в отсутствии дубликатов в данных в таблице, препятствующих созданию индекса.
В остальных случаях попробуйте пересоздать индекс, используя команду [reindex concurrently](https://www.postgresql.org/docs/current/sql-reindex.html).

Если невалидный индекс находится на секционированной таблице, создайте недостающие индексы на остальных секциях
и присоедините их командой [alter index ... attach partition](https://postgrespro.ru/docs/postgresql/17/sql-alterindex):
секционированный индекс станет валидным автоматически, как только будут присоединены индексы всех секций.
