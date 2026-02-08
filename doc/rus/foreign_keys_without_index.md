# Проверка присутствия внешних ключей без индексов

## Особенности создания внешних ключей

Когда [создается ограничение внешнего ключа](https://postgrespro.ru/docs/postgresql/17/ddl-constraints#DDL-CONSTRAINTS-FK),
то автоматически **не** создается индекс на столбец (или группу столбцов) с внешним ключом.
Требования создавать такой индекс вручную тоже нет.

## Почему нужно создавать внешние ключи с индексами

Если у столбца с внешним ключом нет индекса, то при поиске строк с одинаковым внешним ключом идет
последовательное сканирование всей таблицы, что снижает производительность.
Также при удалении данных из главной таблицы идет проверка на ссылочную целостность,
что тоже вызывает последовательное сканирование связанной таблицы с внешним ключом без индекса на нем.
При этом нужно оценить, насколько часто идет поиск или изменение данных по этому столбцу и размер самой таблицы.
Если поиск выполняется редко и/или таблица, содержащая столбец с внешним ключом, небольшого размера,
то добавление индекса может не улучшить производительность.

## SQL запрос

- [foreign_keys_without_index.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_without_index.sql)

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

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## Как исправить

Создайте недостающие индексы на внешние ключи.
Если вы на 100% уверены, что такой индекс вам не нужен, зафиксируйте его как исключение.
