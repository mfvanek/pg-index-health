# Проверка дублирующихся внешних ключей в таблицах

## Как они появляются и почему нужно избавляться от дублирующихся внешних ключей

Внешние ключи могут быть созданы по нескольким атрибутам целевой таблицы — ссылка на ограничение по нескольким столбцам.
При этом возможна ошибка — создание другого внешнего ключа с такими же атрибутами.
Дублирование сущностей увеличивает когнитивную сложность и затрудняет поддержку и развитие схемы данных в будущем.

## SQL запрос

- [duplicated_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_foreign_keys.sql)

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

alter table if exists demo.order_item
    add constraint c_order_item_fk_order_id_duplicate
        foreign key (order_id) references demo.orders (id);

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

alter table if exists demo.order_item_partitioned
    add constraint c_order_item_partitioned_fk_order_id_created_at_duplicate
        foreign key (order_id, created_at) references demo.orders_partitioned (id, created_at);

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## Как исправить

Выберите внешний ключ, который хотите оставить, и удалите дубликат.
