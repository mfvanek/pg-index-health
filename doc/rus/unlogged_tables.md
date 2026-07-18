# Проверка наличия нелогируемых таблиц

Нелогируемые таблицы (UNLOGGED) не поддерживаются механизмом Write-Ahead Log (WAL),
поэтому данные в них не реплицируются на резервные серверы и автоматически удаляются
при аварийном завершении работы сервера.
Такие таблицы не подходят для хранения постоянных данных.

Смотри также https://www.postgresql.org/docs/current/sql-createtable.html#SQL-CREATETABLE-UNLOGGED

## SQL запрос

- [unlogged_tables.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unlogged_tables.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных/интеграционных тестах)

## Поддержка секционированных таблиц

Применима как к обычным, так и к секционированным нелогируемым таблицам.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create unlogged table demo.unlogged_table(
    id   bigint generated always as identity primary key,
    info text
);

-- Пример нелогируемой секционированной таблицы
create unlogged table demo.unlogged_partitioned(
    id      bigint not null,
    created date   not null
) partition by range (created);

create unlogged table demo.unlogged_partitioned_2025
    partition of demo.unlogged_partitioned
    for values from ('2025-01-01') to ('2026-01-01');
```

## Как исправить

Преобразуйте нелогируемую таблицу в обычную (логируемую):

```sql
alter table demo.unlogged_table set logged;
```

Если таблица была намеренно создана как нелогируемая для повышения производительности
(например, для промежуточных или временных данных), рассмотрите использование
временной таблицы, которая явно указывает на временный характер данных:

```sql
create temporary table tmp_staging(
    id   bigint generated always as identity primary key,
    info text
);
```
