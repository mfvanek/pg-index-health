# Проверка наличия ограничений, которые не были провалидированы

## Почему данные могут не соответствовать существующим ограничениям

- [Источник](https://habr.com/ru/articles/800121/)

Некоторые типы ограничений (в настоящее время это ограничение-проверка `CHECK` и, с некоторыми оговорками, `FOREIGN KEY`),
могут создаваться с ключом `NOT VALID`.
При создании ограничений для больших таблиц проверка уже имеющихся данных может быть длительной,
поэтому разработчики пользуются удобным механизмом в PostgreSQL и разделяют процессы создания ограничения и проверки всех данных.

Важно отметить, что ограничение сразу после создания будет действовать при добавлении или изменении данных
и любая из этих операций будет прервана, если новые данные не соответствуют ограничению.
Однако на уровне базы данных будет установлен признак, что ограничение не было проверено для всех данных,
пока оно не будет проверено командой `VALIDATE CONSTRAINT`.

## SQL запрос

- [not_valid_constraints.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/not_valid_constraints.sql)

## Тип проверки

- **runtime** (имеет смысл запускать на работающем инстансе БД после выполнения миграций)
- **static** (может выполняться в компонентных\интеграционных тестах для проверки корректности миграций)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Для обычных (не секционированных) таблиц

create table if not exists demo.clients(
    id bigint primary key generated always as identity,
    first_name varchar(255) not null,
    last_name varchar(255) not null
);

create table if not exists demo.accounts(
    id bigint primary key generated always as identity,
    client_id bigint not null,
    account_number varchar(50) not null,
    account_balance numeric(22, 2) not null default 0
);

-- Внешний ключ, созданный с ключом NOT VALID: уже имеющиеся строки не проверяются
alter table if exists demo.accounts
    add constraint c_accounts_fk_client_id_not_validated_yet
    foreign key (client_id) references demo.clients (id) not valid;

-- Ограничение-проверка, созданное с ключом NOT VALID: уже имеющиеся строки не проверяются
alter table if exists demo.accounts
    add constraint c_accounts_chk_client_id_not_validated_yet
    check (client_id > 0) not valid;

-- Для секционированных таблиц

create table if not exists demo.orders_partitioned(
    id         bigint      not null generated always as identity,
    user_id    bigint      not null,
    status     int         not null,
    created_at timestamptz not null default current_timestamp,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.orders_default
    partition of demo.orders_partitioned default;

-- Ограничение-проверка с ключом NOT VALID на самой секционированной (родительской) таблице
alter table if exists demo.orders_partitioned
    add constraint c_orders_chk_status_not_validated_yet
    check (status >= 0) not valid;
```

## Как исправить

Чтобы перевести ограничение в валидное состояние, выполните команду [`VALIDATE CONSTRAINT`](https://postgrespro.ru/docs/postgresql/17/sql-altertable).
Она проверит все уже имеющиеся в таблице строки на соответствие ограничению, но, в отличие от создания ограничения,
не блокирует чтение и запись — берётся только блокировка уровня `SHARE UPDATE EXCLUSIVE`.

Для обычных таблиц:

```sql
alter table demo.accounts validate constraint c_accounts_fk_client_id_not_validated_yet;
alter table demo.accounts validate constraint c_accounts_chk_client_id_not_validated_yet;
```

Для секционированных таблиц валидируйте ограничение на самой секционированной (родительской) таблице:

```sql
alter table demo.orders_partitioned validate constraint c_orders_chk_status_not_validated_yet;
```

Если команда `VALIDATE CONSTRAINT` завершается ошибкой, значит в таблице есть данные, не соответствующие ограничению.
Найдите и исправьте такие строки (либо удалите само ограничение, если оно ошибочно), после чего повторите валидацию.
