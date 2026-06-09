# Проверка наличия неиспользуемых индексов в таблицах

## Как появляются неиспользуемые индексы

PostgreSQL может никогда не использовать некоторые индексы.

Причин у этого может быть много:
* наличие аналогичных индексов (дублирующихся или пересекающихся)
* малый размер таблицы
* низкая селективность индекса и другие.

## Почему нужно избавляться от неиспользуемых индексов

При изменении данных обновляется [каждый индекс](https://postgrespro.ru/docs/postgresql/17/btree), таким образом лишние индексы снижают производительность.
Так же каждый индекс занимает место на диске.

## SQL запрос

- [unused_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unused_indexes.sql)

## Тип проверки

- **runtime** (требует наличия накопленной статистики)

В кластере БД проверка должна выполняться на каждом хосте.

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы. Проверка выполняется на каждой секции.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Для обычных (не секционированных) таблиц

create table if not exists demo.clients (
    id bigint not null primary key generated always as identity,
    last_name varchar(255) not null,
    first_name varchar(255) not null,
    email varchar(200) not null,
    phone varchar(50) not null
);

create table if not exists demo.accounts (
    id bigint not null primary key generated always as identity,
    client_id bigint not null references demo.clients (id),
    account_number varchar(50) not null unique,
    account_balance numeric(22, 2) not null default 0,
    deleted boolean not null default false
);

-- Индексы, которые никто не использует
create index if not exists i_clients_last_name
    on demo.clients (last_name);
create index if not exists i_clients_last_first
    on demo.clients (last_name, first_name);
create index if not exists i_accounts_account_number
    on demo.accounts (account_number);
create index if not exists i_accounts_account_number_not_deleted
    on demo.accounts (account_number) where not deleted;
create index if not exists i_accounts_number_balance_not_deleted
    on demo.accounts (account_number, account_balance) where not deleted;

-- Наполнение таблиц данными
insert into demo.clients (last_name, first_name, email, phone)
select
    'last_name_' || g.id,
    'first_name_' || g.id,
    'client_' || g.id || '@example.com',
    '+7900' || lpad(g.id::text, 7, '0')
from generate_series(1, 1000) as g(id);

insert into demo.accounts (client_id, account_number)
select c.id, '40702810' || lpad(c.id::text, 12, '0')
from demo.clients c;

-- Обновляем статистику, чтобы значения в pg_stat_*_indexes стали актуальными
vacuum analyze demo.clients;
vacuum analyze demo.accounts;

-- Для секционированных таблиц

create table if not exists demo.entity_reference(
    ref_type varchar(32),
    ref_value varchar(64),
    creation_date timestamptz not null,
    entity_id varchar(64) not null
) partition by range (creation_date);

create index if not exists idx_entity_reference_type_value
    on demo.entity_reference (ref_type, ref_value);
create index if not exists idx_entity_reference_entity_value
    on demo.entity_reference (entity_id, ref_value);

create table if not exists demo.entity_reference_default
    partition of demo.entity_reference default;
```

## Как исправить

Удалите неиспользуемые индексы.

```sql
drop index concurrently if exists demo.i_clients_last_name;
```

Используйте `drop index concurrently`, чтобы не блокировать запись в таблицу на время удаления.

Перед удалением убедитесь, что статистика накоплена на всех хостах кластера за достаточно длительный период
(в том числе с учётом редких операций — отчётов, ночных задач, обслуживания), а также что после последнего сброса
статистики (`pg_stat_reset()`) прошло достаточно времени.
Индекс, который не использовался на primary хосте, может активно использоваться на реплике.
