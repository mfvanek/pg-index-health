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

-- Для обычных (не секционированных) таблиц

create table if not exists demo.orders(
    id bigint primary key generated always as identity,
    user_id bigint not null,
    shop_id bigint not null,
    status int not null,
    created_at timestamptz not null default current_timestamp
);

create table if not exists demo.order_item(
    id bigint primary key generated always as identity,
    order_id bigint not null references demo.orders (id),
    price decimal(22, 2) not null default 0,
    amount int not null default 0,
    sku varchar(255) not null,
    warehouse_id int
);

create index if not exists idx_order_item_sku on demo.order_item (sku);

create index if not exists idx_order_item_sku_warehouse_id on demo.order_item (sku, warehouse_id);

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

create table if not exists demo.order_item_partitioned(
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

create index if not exists idx_order_item_partitioned_sku
    on demo.order_item_partitioned (sku, created_at);

create index if not exists idx_order_item_partitioned_sku_warehouse_id
    on demo.order_item_partitioned (sku, created_at, warehouse_id);

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## Как исправить

TODO
