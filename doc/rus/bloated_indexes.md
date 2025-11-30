# Проверка раздувания индексов в таблицах

## Почему раздуваются индексы

Частое обновление данных может привести к раздуванию индекса по той же причине, что и
к [раздуванию таблицы](bloated_tables.md)

## Почему нужно следить за раздуванием индексов

Если индекс раздувается, то снижается производительность (приходится читать больше страниц с диска).

## SQL запрос

- [bloated_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_indexes.sql)

## Тип проверки

- **runtime** (требует наличия накопленной статистики)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы. Процент bloat'а считается для каждой секции отдельно.

## Как работает эта проверка

Для выполнения запроса пользователю необходимы права на чтение проверяемых таблиц.

### Принцип работы

Выполняется SQL-запрос к таблицам системной схемы pg_catalog. Они содержат статистическую информацию об основных объектах:
таблицах, индексах, столбцах.

Сначала в запросе собираются данные о B-tree индексах в указанной схеме.
Каждый столбец индекса рассматривается отдельно.
Индексы связываются с соответствующими столбцами таблицы.
Собранные параметры позволяют оценить количество страниц, которое должно быть использовано индексом.
Оно сравнивается с фактическим количеством страниц. Учитываются те индексы, по которым доступна статистика.
После этого вычисляется разница между фактическим и оцененным количеством страниц индекса и по ней процент раздутости индекса. 
Если он превышает заданное значение (дефолтное составляет 10%), то индекс считается раздутым.
Результаты сортируются по имени таблицы и имени индекса.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Для обычных (не секционированных) таблиц

create table if not exists demo.orders
(
    id bigint primary key generated always as identity,
    user_id bigint not null,
    shop_id bigint not null,
    status int not null,
    created_at timestamptz not null default current_timestamp
);

create table if not exists demo.order_item
(
    id bigint primary key generated always as identity,
    order_id bigint not null references demo.orders (id),
    price decimal(22, 2) not null default 0,
    amount int not null default 0,
    sku varchar(255) not null,
    warehouse_id int
);

create index if not exists idx_order_item_order_id
    on demo.order_item (order_id);

create index if not exists idx_order_item_warehouse_id_without_nulls
    on demo.order_item (warehouse_id) where warehouse_id is not null;

-- Наполнение данными

insert into demo.orders (user_id, shop_id, status)
select
    (ids.id % 10) + 1 as user_id,
    (ids.id % 4) + 1 as shop_id,
    1 as status
from generate_series(1, 10000) ids (id);

insert into demo.order_item (order_id, price, amount, sku)
select
    id as order_id,
    (random() + 1) * 1000.0 as price,
    (random() * 10) + 1 as amount,
    md5(random()::text) as sku
from demo.orders;

insert into demo.order_item (order_id, price, amount, sku)
select
    id as order_id,
    (random() + 1) * 2000.0 as price,
    (random() * 5) + 1 as amount,
    md5((random() + 1)::text) as sku
from demo.orders where id % 2 = 0;

-- собираем статистику
vacuum analyze demo.orders, demo.order_item;

-- обновляем статус у нескольких заказов
update demo.orders
set status = 2 -- paid order
where
    status = 1 -- new order
  and id in (
    select id from demo.orders where id % 4 = 0 order by id limit 10000);

update demo.order_item
set warehouse_id = case when order_id % 8 = 0 then 1 else 2 end
where
    warehouse_id is null
  and order_id in (
    select id from demo.orders
    where
        status = 2
      and created_at >= current_timestamp - interval '1 day');

-- собираем статистику
vacuum analyze demo.orders, demo.order_item;

-- Для секционированных таблиц

create table if not exists demo.orders_partitioned
(
    id         bigint not null generated always as identity,
    user_id    bigint      not null,
    shop_id    bigint      not null,
    status     int         not null,
    created_at timestamptz not null default current_timestamp,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.orders_default
    partition of demo.orders_partitioned default;

create table if not exists demo.order_item_partitioned
(
    id           bigint generated always as identity,
    order_id     bigint         not null,
    created_at   timestamptz    not null,
    price        decimal(22, 2) not null default 0,
    amount       int            not null default 0,
    sku          varchar(255)   not null,
    warehouse_id int,
    primary key (id, created_at),
    constraint fk_order_item_order_id foreign key (order_id, created_at)
        references demo.orders_partitioned (id, created_at)
) partition by range (created_at);

create index if not exists idx_order_item_partitioned_order_id
    on demo.order_item_partitioned (order_id);

create index if not exists idx_order_item_partitioned_warehouse_id_without_nulls
    on demo.order_item_partitioned (warehouse_id) where warehouse_id is not null;

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;

-- Наполнение данными

insert into demo.orders_partitioned (user_id, shop_id, status)
select (ids.id % 10) + 1 as user_id,
       (ids.id % 4) + 1  as shop_id,
       1                 as status
from generate_series(1, 10000) ids (id);

insert into demo.order_item_partitioned (order_id, created_at, price, amount, sku)
select id as order_id, created_at,
       (random() + 1) * 1000.0 as price,
       (random() * 10) + 1     as amount,
       md5(random()::text)     as sku
from demo.orders_partitioned;

insert into demo.order_item_partitioned (order_id, created_at, price, amount, sku)
select id as order_id, created_at,
       (random() + 1) * 2000.0   as price,
       (random() * 5) + 1        as amount,
       md5((random() + 1)::text) as sku
from demo.orders_partitioned
where id % 2 = 0;

-- собираем статистику
vacuum analyze demo.orders_partitioned, demo.order_item_partitioned;

-- обновляем статус у нескольких заказов
update demo.orders_partitioned
set status = 2 -- paid order
where status = 1 -- new order
  and id in (select id
             from demo.orders_partitioned
             where id % 4 = 0
             order by id
             limit 10000);

update demo.order_item_partitioned
set warehouse_id = case when order_id % 8 = 0 then 1 else 2 end
where warehouse_id is null
  and order_id in (select id
                   from demo.orders_partitioned
                   where status = 2
                     and created_at >= current_timestamp - interval '1 day');

-- собираем статистику
vacuum analyze demo.orders_partitioned, demo.order_item_partitioned;
```

## Как исправить

Необходимо следить за bloat'ом и регулярно выполнять пересоздание индексов через команду [reindex concurrently](https://postgrespro.ru/docs/postgresql/current/sql-reindex).
