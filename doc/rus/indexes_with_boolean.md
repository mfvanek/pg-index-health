# Проверка индексов на boolean-столбцы в таблицах

## Почему такие индексы обычно не нужны

boolean  столбец может принимать только два значения: true и  false. Поэтому у такого индекса низкая кардинальность.
Индексы на столбцах с низкой кардинальностью редко используются планировщиком запросов, так как они незначительно ускоряют поиск данных. Планировщик, скорее всего, выберет полное сканирование при фильтрации по boolean-столбцу, если таблица небольшая или если запрос требует большого процента строк.

## Как лучше организовать индекс, включащий поиск по boolean-столбцу

Если строк с определенным значением столбца типа boolean гораздо меньше половины и при поиске чаще ищутся именно такие строки,
то [можно создать частичный индекс](https://postgrespro.ru/docs/postgrespro/17/indexes-partial)

## SQL запрос

- [indexes_with_boolean.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_boolean.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create sequence if not exists demo.accounts_seq;

create table if not exists demo.accounts (
    id bigint not null primary key default nextval('demo.accounts_seq'),
    client_id bigint not null,
    account_number varchar(50) not null unique,
    account_balance numeric(22,2) not null default 0,
    deleted boolean not null default false
);

create index if not exists i_accounts_deleted
    on demo.accounts (deleted);

create unique index if not exists i_accounts_account_number_deleted
    on demo.accounts (account_number, deleted);

create table if not exists demo.dict(
    ref_type int not null primary key,
    description text
);

create table if not exists demo.partitioned_table(
    ref_value varchar(64) not null,
    ref_type bigserial not null references demo.dict(ref_type),
    creation_date timestamp with time zone not null,
    entity_id varchar(64) not null,
    deleted boolean not null,
    primary key (ref_value, ref_type, creation_date, entity_id)
) partition by range (creation_date);

create index if not exists idx_t1_deleted on demo.partitioned_table(deleted);

create table if not exists demo.t1_default
    partition of demo.partitioned_table default;
```

## Как исправить

Рассмотрите создание и использование частичных индексов с предикатом по boolean-полю.
