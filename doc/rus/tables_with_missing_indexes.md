# Проверка таблиц на нехватку индексов

## Как понять, что нужен индекс

Представление pg_stat_all_tables [содержит статистику по обращениям к таблицам](https://postgrespro.ru/docs/postgresql/17/monitoring-stats#MONITORING-PG-STAT-ALL-INDEXES-VIEW).
Отсюда можно получить данные о том, насколько часто используется последовательное сканирование и поиск с помощью индексов.
Если таблица часто сканируется последовательно, то стоит добавить индексы для соответствующих столбцов.

## Почему нужно следить за использованием индексов

Если статистика показывает много запросов с последовательным сканированием таблицы,
то добавление индекса может ускорить поиск и снизить нагрузку на БД.

## SQL запрос

- [tables_with_missing_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_missing_indexes.sql)

## Тип проверки

- **runtime** (требует наличия накопленной статистики)

В кластере БД проверка должна выполняться на каждом хосте.

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы. Проверка выполняется на каждой секции.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."table_with_missing_index"
(
    id bigint not null primary key,
    first_name text,
    last_name text
);

create table if not exists demo."table_with_missing_index_partitioned"
(
    id integer not null primary key,
    first_name text,
    last_name text
) partition by range (id);

create table if not exists demo."table_with_missing_index_partitioned_20"
    partition of demo."duplicated_indexes_partitioned"
    for values from (1) to (21);
    
insert into demo."table_with_missing_index_partitioned_1_20" (id, first_name, last_name) values (generate_series(1, 20),'first', 'last');
```
