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

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Для обычных (не секционированных) таблиц

create table if not exists demo.orders(
    id bigint primary key generated always as identity,
    user_id bigint not null,
    shop_id bigint not null,
    status int not null,
    created_at timestamptz not null default current_timestamp
);

-- Наполнение данными

insert into demo.orders (user_id, shop_id, status)
select
    (ids.id % 10) + 1 as user_id,
    (ids.id % 4) + 1 as shop_id,
    1 as status
from generate_series(1, 10000) ids (id);

DO $$
    DECLARE
        min_shop_id bigint;
        r record;
    BEGIN
        -- один раз считаем минимальный shop_id
        SELECT MIN(shop_id) INTO min_shop_id FROM demo.orders;

        -- цикл 500 раз
        FOR i IN 1..500 LOOP
                SELECT *
                INTO r
                FROM demo.orders
                WHERE shop_id = min_shop_id;

                SELECT *
                INTO r
                FROM demo.orders
                WHERE shop_id = 12345;
            END LOOP;
    END $$;

-- собираем статистику
vacuum analyze demo.orders;

-- Для секционированных таблиц

create table if not exists demo.orders_partitioned(
    id         bigint not null generated always as identity,
    user_id    bigint      not null,
    shop_id    bigint      not null,
    status     int         not null,
    created_at timestamptz not null default current_timestamp,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.orders_default
    partition of demo.orders_partitioned default;

-- Наполнение данными

insert into demo.orders_partitioned (user_id, shop_id, status)
select (ids.id % 10) + 1 as user_id,
       (ids.id % 4) + 1  as shop_id,
       1                 as status
from generate_series(1, 10000) ids (id);

DO $$
    DECLARE
        min_shop_id bigint;
        r record;
    BEGIN
        -- один раз считаем минимальный shop_id
        SELECT MIN(shop_id) INTO min_shop_id FROM demo.orders_partitioned;

        -- цикл 500 раз
        FOR i IN 1..500 LOOP
                SELECT *
                INTO r
                FROM demo.orders_partitioned
                WHERE shop_id = min_shop_id;

                SELECT *
                INTO r
                FROM demo.orders_partitioned
                WHERE shop_id = 12345;
            END LOOP;
    END $$;

-- собираем статистику
vacuum analyze demo.orders_partitioned;
```

## Как исправить

Проанализируйте запросы к проблемной таблице и добавьте индекс на столбцы, по которым чаще всего выполняется фильтрация.
В примере выше поиск идёт по `shop_id`, поэтому индекс стоит создать именно на нём.

```sql
create index concurrently if not exists idx_orders_shop_id
    on demo.orders (shop_id);
```

Используйте `create index concurrently`, чтобы не блокировать запись в таблицу на время создания индекса.

Эта проверка лишь сигнализирует о большом количестве последовательных сканирований — она не указывает,
какой именно индекс нужен. Подбирайте состав и тип индекса исходя из реальных запросов (например, с помощью `explain analyze`).
Помните, что лишние индексы замедляют запись, поэтому добавляйте только действительно необходимые.
