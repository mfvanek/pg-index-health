# Проверка внешних ключей, у которых есть расхождения в типах столбцов

## Почему типы столбцов у внешних ключей должны совпадать

Типы колонок в ссылающемся и целевом отношении должны совпадать.
Колонка с типом `integer` должна ссылаться на колонку с типом `integer`.
Это исключает лишние конвертации на уровне СУБД и в коде приложения, снижает количество ошибок,
которые могут появляться из-за несоответствия типов в будущем.

Можно возразить, достаточно, чтобы в ссылающемся отношении типы колонок были совместимы и не меньше, чем в целевом отношении.
Например, колонка с типом `bigint` может ссылаться на колонку с типом `integer`.
**Так делать не следует**.
Необходимо учитывать, что разные типы будут попадать в код приложения, особенно при автоматической генерации моделей.

## SQL запрос

- [foreign_keys_with_unmatched_column_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_with_unmatched_column_type.sql)

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
    order_id int not null references demo.orders (id),
    price decimal(22, 2) not null default 0,
    amount int not null default 0,
    sku varchar(255) not null,
    warehouse_id int
);

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
    order_id     int         not null,
    created_at   timestamptz    not null,
    price        decimal(22, 2) not null default 0,
    amount       int            not null default 0,
    sku          varchar(255)   not null,
    warehouse_id int,
    primary key (id, created_at),
    constraint fk_order_item_order_id foreign key (order_id, created_at)
      references demo.orders_partitioned (id, created_at)
) partition by range (created_at);

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## Как исправить

Выровняйте типы колонок в ссылающемся и целевом отношении.
