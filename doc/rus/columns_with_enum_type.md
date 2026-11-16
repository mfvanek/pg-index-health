# Проверка наличия столбцов с типом enum в таблицах

## Почему не стоит использовать

[Перечисляемые типы](https://www.postgresql.org/docs/current/datatype-enum.html) выглядят удобно, но их сложно развивать:

- В enum можно добавлять новые значения, но существующие значения нельзя удалить или изменить их порядок.
- Удаление значения требует пересоздания типа и перезаписи всех зависимых столбцов и индексов.
- Любое изменение списка допустимых значений — это DDL миграция, а не простое обновление данных.
- Enum менее гибок, чем внешний ключ на таблицу-справочник: к значению нельзя добавить дополнительные атрибуты или пометить его устаревшим.

Лучше использовать столбец типа `text` с ограничением `check` или внешний ключ на таблицу-справочник.

Проверка также находит столбцы, являющиеся массивами enum (в том числе многомерными).

Смотрите также правило [prefer-text-with-check-to-enum](https://github.com/kristiandupont/schemalint/blob/main/src/rules/README.md#prefer-text-with-check-to-enum) в schemalint.

## SQL запрос

- [columns_with_enum_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_enum_type.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create type demo.order_status as enum ('new', 'paid', 'shipped');

create table if not exists demo."bad-orders"
(
    id bigint generated always as identity primary key,
    "status-bad" demo.order_status not null, -- enum
    status_history demo.order_status[], -- массив enum
    status_text text not null check (status_text in ('new', 'paid', 'shipped')) -- рекомендуемая альтернатива
);

-- Индекс по столбцу с типом enum не попадает в результат
create index if not exists i_bad_orders_status on demo."bad-orders" ("status-bad");

-- Представления над столбцами с типом enum не попадают в результат
create or replace view demo.v_bad_orders as select id, "status-bad" from demo."bad-orders";

-- Для секционированных таблиц

create table if not exists demo."bad-orders_partitioned"
(
    id bigint not null,
    created_at timestamptz not null,
    "status-bad" demo.order_status not null,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo."bad-orders_partitioned_default"
    partition of demo."bad-orders_partitioned" default;
```

## Как исправить

Измените тип целевого столбца на `text` и ограничьте допустимые значения с помощью `check`:

```sql
-- Представления, зависящие от столбца, нужно предварительно удалить, а затем создать заново
drop view if exists demo.v_bad_orders;

alter table demo."bad-orders"
    alter column "status-bad" type text using "status-bad"::text,
    add constraint c_bad_orders_status_valid check ("status-bad" in ('new', 'paid', 'shipped'));
```

Если список значений часто меняется или значениям нужны дополнительные атрибуты, используйте внешний ключ на таблицу-справочник.
Когда тип enum больше не используется ни в одном столбце, удалите его с помощью `drop type`.
Не забудьте внести необходимые правки в приложение, работающее с БД.
