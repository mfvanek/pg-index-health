# Проверка наличия таблиц, не связанных с другими таблицами

## Почему таблицы без связей нужно отслеживать

Отсутствие связей в таблице может быть следствием ошибки: забыли добавить внешние ключи.
Все таблицы в базе данных отражают модель данных — связи нужны для консистентных изменений в таблицах.
Также внешние ключи дают возможность быстро и верно находить данные, относящиеся к одной сущности.

Если таблицы без связей созданы намеренно, то их нужно просто проигнорировать.

## SQL запрос

- [tables_not_linked_to_others.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_not_linked_to_others.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Связанные таблицы: clients и accounts соединены внешним ключом,
-- поэтому проверка их не покажет.
create table if not exists demo.clients (
    id bigint not null primary key generated always as identity,
    last_name varchar(255) not null,
    first_name varchar(255) not null
);

create table if not exists demo.accounts (
    id bigint not null primary key generated always as identity,
    client_id bigint not null references demo.clients (id),
    account_number varchar(50) not null unique
);

-- Таблица без единой связи с другими таблицами — попадёт в результаты проверки.
create table if not exists demo.standalone (
    id bigint not null primary key generated always as identity,
    name varchar(255) not null
);

-- Секционированная таблица без связей.
-- Проверка покажет только родительскую таблицу, секции игнорируются.
create table if not exists demo.entity_reference(
    ref_type varchar(32) not null,
    ref_value varchar(64) not null,
    creation_date timestamptz not null,
    entity_id varchar(64) not null,
    primary key (ref_type, ref_value, creation_date, entity_id)
) partition by range (creation_date);

create table if not exists demo.entity_reference_default
    partition of demo.entity_reference default;
```

## Как исправить

Убедитесь, что отсутствие связей не является ошибкой моделирования (забытый внешний ключ).
Если таблица должна ссылаться на другую сущность, добавьте внешний ключ.

```sql
alter table demo.standalone
    add column client_id bigint not null references demo.clients (id);
```

Если таблица без связей создана намеренно (например, справочник, лог или служебная таблица),
просто исключите её из проверки с помощью подходящего предиката (`SkipTablesByNamePredicate` и т.п.).
