# Проверка наличия неиспользуемых индексов в таблицах

## Как появляются неиспользуемые индексы

PostgreSQL может никогда не использовать некоторые индексы.

Причин у этого может быть много:
* наличие аналогичных индексов (дублирующихся или пересекающихся)
* малый размер таблицы
* низкая селективность индекса и другие.

## Почему нужно избавляться от неиспользуемых индексов

При изменении данных обновляется [каждый индекс](https://postgrespro.ru/docs/postgresql/17/btree), таким образом лишние индексы снижают производительность.
Так же каждый индекс занимает место на диске.

## SQL запрос

- [unused_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unused_indexes.sql)

## Тип проверки

- **runtime** (требует наличия накопленной статистики)

В кластере БД проверка должна выполняться на каждом хосте.

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы. Проверка выполняется на каждой секции.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."duplicated_indexes"
(
    id integer not null primary key,
    first_name text,
    last_name text
);

create index if not exists i_duplicated_indexes_last_first
                    on demo."duplicated_indexes" (last_name, first_name);
create index if not exists i_duplicated_indexes_last_not_deleted
                    on demo."duplicated_indexes" (last_name, first_name) where not deleted;
                    
create table if not exists demo."duplicated_indexes_partitioned"
(
    id integer not null primary key,
    first_name text,
    last_name text
) partition by range (id);

create index if not exists i_duplicated_indexes_last_first
                    on demo."duplicated_indexes_partitioned" (last_name, first_name);
create index if not exists i_duplicated_indexes_last_not_deleted
                    on demo."duplicated_indexes_partitioned" (last_name, first_name) where not deleted;

create table if not exists demo."duplicated_indexes_partitioned_1_20"
    partition of demo."duplicated_indexes_partitioned"
    for values from (1) to (21);
    
insert into demo."duplicated_indexes" (id, first_name, last_name) values (generate_series(1, 20),'first', 'last');

insert into demo."duplicated_indexes_partitioned" (id, first_name, last_name) values (generate_series(1, 20),'first', 'last');
```
