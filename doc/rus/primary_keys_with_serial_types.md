# Проверка наличия первичных ключей с типом serial

## Почему первичный ключ не стоит создавать с типом serial

Первичный ключ типа serial [создает проблемы](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_serial):

- не соответствует стандарту SQL, а значит код нельзя переиспользовать;
- может вызывать ошибки, если манипуляции с таблицей включены в скрипты при деплое;
- трудно вносить изменения в ПК с таким типом.

Есть [другой вариант создания первичного ключа](https://postgrespro.ru/docs/postgresql/17/sql-createtable#SQL-CREATETABLE-PARMS-GENERATED-IDENTITY).
Именно его нужно использовать.

## SQL запрос

- [primary_keys_with_serial_types.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/primary_keys_with_serial_types.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Плохо: первичный ключ с типом bigserial — проверка его покажет.
create table if not exists demo.bad_accounts (
    id bigserial not null primary key,
    name varchar(255) not null
);

-- Хорошо: первичный ключ через generated always as identity — проверка его не покажет.
create table if not exists demo.good_accounts (
    id bigint not null generated always as identity primary key,
    name varchar(255) not null
);

-- Секционированная таблица с serial-первичным ключом.
-- Проверка покажет только родительскую таблицу, секции игнорируются.
create table if not exists demo.bad_partitioned (
    id bigserial not null,
    created_at timestamptz not null default now(),
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.bad_partitioned_default
    partition of demo.bad_partitioned default;
```

## Как исправить

Замените `serial`/`bigserial` на современный синтаксис `generated always as identity`.

Для новых таблиц просто объявляйте первичный ключ так:

```sql
create table demo.good_accounts (
    id bigint not null generated always as identity primary key,
    name varchar(255) not null
);
```

Существующий serial-столбец можно перевести на identity без пересоздания таблицы.
Тип столбца (`integer`/`bigint`) при этом не меняется — serial лишь добавляет к нему значение по умолчанию
из последовательности и владение этой последовательностью:

```sql
-- 1. Убираем default, ссылающийся на последовательность
alter table demo.bad_accounts
    alter column id drop default;

-- 2. Удаляем «осиротевшую» последовательность
drop sequence demo.bad_accounts_id_seq;

-- 3. Делаем столбец identity-колонкой
alter table demo.bad_accounts
    alter column id add generated always as identity;

-- 4. Синхронизируем внутренний счётчик identity с текущим максимумом
select setval(pg_get_serial_sequence('demo.bad_accounts', 'id'), coalesce(max(id), 1))
from demo.bad_accounts;
```

Имя последовательности по умолчанию — `<таблица>_<столбец>_seq`; уточнить его можно через
`pg_get_serial_sequence('demo.bad_accounts', 'id')`.
