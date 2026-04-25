# Проверка присутствия индексов, включающих null значения

## Особенности создания b-tree индексов

По умолчанию Postgres [включает null значения в btree-индексы](https://postgrespro.ru/docs/postgresql/17/indexes-ordering).

## Почему нужно удалять null значения из индексов

Это может существенно уменьшить размер индекса в том случае если null значение встречается часто.
Частичный индекс, из которого исключены null значения будет оптимальнее, потому что при поиске
распространённого значения [индекс всё равно не будет использоваться](https://postgrespro.ru/docs/postgresql/17/indexes-partial).
Поиск будет проходить быстрее. Индекс будет занимать меньше места на диске.

## SQL запрос

- [indexes_with_null_values.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_null_values.sql)

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
    deleted_at timestamptz
);

create index if not exists i_accounts_deleted_at
    on demo.accounts (deleted_at);

create unique index if not exists i_accounts_account_number_deleted_at
    on demo.accounts (account_number, deleted_at);

create table if not exists demo.dict(
    ref_type int not null primary key,
    description text
);

create table if not exists demo.partitioned_table(
    ref_value varchar(64) not null,
    ref_type bigserial not null references demo.dict(ref_type),
    creation_date timestamp with time zone not null,
    entity_id varchar(64) not null,
    deleted_at timestamptz,
    primary key (ref_value, ref_type, creation_date, entity_id)
) partition by range (creation_date);

create index if not exists idx_t1_deleted_at on demo.partitioned_table(deleted_at);

create table if not exists demo.t1_default
    partition of demo.partitioned_table default;
```

## Как исправить

Используйте частичные индексы для исключения null значений.
