# Проверка наличия столбцов с типами timestamp (without time zone) или timetz в таблицах

## Почему не стоит использовать

- Не используйте тип `timestamp` для хранения временных меток.
  Вместо этого используйте `timestamptz` (также известный как `timestamp with time zone`).
- Не используйте тип `timetz`. Вероятнее всего, вам нужен `timestamptz` вместо этого.

Смотрите также:

* https://habr.com/ru/articles/772954/
* https://neon.com/postgresql/postgresql-tutorial/postgresql-timestamp
* https://wiki.postgresql.org/wiki/Don't_Do_This#Don.27t_use_timestamp_.28without_time_zone.29

## SQL запрос

- [columns_with_timestamp_or_timetz_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_timestamp_or_timetz_type.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."bad-time"
(
    id bigint generated always as identity primary key,
    "created-bad" timestamp not null default now(),
    created_at timestamptz not null default now(),
    created_at_good timestamp with time zone not null default now(),
    time_bad timetz not null
);

create table if not exists demo."bad-time_partitioned"
(
    id uuid not null primary key,
    "created-bad" timestamp not null default now(),
    created_at timestamptz not null default now(),
    created_at_good timestamp with time zone not null default now(),
    time_bad timetz not null
) partition by hash (id);

create table if not exists demo."bad_time_partitioned_hash_p0"
    partition of demo."bad-time_partitioned" for values with (modulus 4, remainder 0);
```
