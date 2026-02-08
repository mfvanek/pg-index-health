# Проверка наличия дублирующихся индексов в таблицах

## Почему могут появиться дубликаты индексов

Индекс на первичный ключ и индексы на ограничения уникальности создаются [автоматически](https://postgrespro.ru/docs/postgresql/17/indexes-unique).
Если подобного рода индексы были созданы еще и вручную, то [получаются дубликаты](https://postgrespro.ru/docs/postgresql/17/sql-createindex).
PostgreSQL не умеет отслеживать такие ситуации и предотвращать создание дубликатов.

## Почему нужно избавляться от дублирующихся индексов

При изменении данных обновляется [каждый индекс](https://postgrespro.ru/docs/postgresql/17/btree).
Таким образом дубликаты снижают производительность, занимают место на диске и
требуют регулярного обслуживания (вакуумирование, переиндексация при распухании).

## SQL запрос

- [duplicated_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_indexes.sql)

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
    sku varchar(255) not null unique, -- индекс будет создан автоматически
    warehouse_id int
);

-- дубликат, который можно (и нужно) удалить
create unique index if not exists idx_order_item_sku on demo.order_item (sku);

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
    unique (sku, created_at),
    constraint fk_order_item_order_id foreign key (order_id, created_at)
      references demo.orders_partitioned (id, created_at)
) partition by range (created_at);

-- дубликат, который можно (и нужно) удалить
create unique index if not exists idx_order_item_partitioned_sku on demo.order_item_partitioned (sku, created_at);

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## Как исправить

Выберите индекс, который хотите оставить, и удалите дубликат.
