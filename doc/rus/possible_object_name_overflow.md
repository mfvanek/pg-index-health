# Проверка наличия объектов с названием максимальной длины

## Почему нужно отслеживать длину названий объектов БД

Максимальный размер идентификатора составляет 63 байта.
Если он превышен, то PostgreSQL без уведомления обрезает слишком длинное название.
Это может привести к ситуации, когда 2 разных объекта становятся идентичными по названию.
Например, если создается миграция, где создаются индексы с длинными названиями,
которые начинаются одинаково, то при использовании выражения `IF NOT EXISTS` может создаться только один объект вместо нескольких.

## SQL запрос

- [possible_object_name_overflow.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/possible_object_name_overflow.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется как на самой секционированной таблице (родительской), так и на отдельных секциях (потомках).

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Идентификатор в PostgreSQL не может быть длиннее 63 байт (max_identifier_length).
-- Имя большей длины молча обрезается до 63 символов, поэтому объекты с именем ровно из 63 символов потенциально опасны.

-- Для обычных (не секционированных) таблиц

create table if not exists demo.accounts(
    id bigint primary key generated always as identity,
    client_id bigint not null,
    account_number varchar(50) not null,
    account_balance numeric(22, 2) not null default 0
);

-- Имя ограничения ровно из 63 символов
alter table if exists demo.accounts
    add constraint num_less_than_million_constraint_with_length_63_1234567890_1234
    check (account_balance < 1000000);

-- Имя материализованного представления ровно из 63 символов
create materialized view if not exists
    demo."accounts-materialized-view-with-length-63-1234567890-1234567890" as (
    select client_id, account_number from demo.accounts);

-- Для секционированных таблиц
-- Имя секционированной таблицы состоит ровно из 63 символов; автоматически создаваемые
-- последовательность, первичный ключ и индексы также получают имена длиной около 63 символов.

create table if not exists demo.entity_long_1234567890_1234567890_1234567890_1234567890_1234567(
    ref_type  varchar(32),
    ref_value varchar(64),
    entity_id bigserial primary key
) partition by range (entity_id);

create index if not exists idx_entity_long_1234567890_1234567890_1234567890_1234567890_123
    on demo.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 (ref_type, ref_value);

create table if not exists demo.entity_default_long_1234567890_1234567890_1234567890_1234567890
    partition of demo.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 default;
```

## Как исправить

Используйте более короткие имена объектов БД — так, чтобы их длина гарантированно не достигала 63 байт (а лучше с запасом).
Особенно внимательно отнеситесь к именам, которые формируются по шаблону и из-за общего префикса могут оказаться
неуникальными после обрезки (например, длинные имена индексов и ограничений, начинающиеся одинаково).

Если объект с именем максимальной длины уже создан, переименуйте его командами `alter ... rename to`,
предварительно убедившись, что обрезка не привела к коллизии с другим объектом:

```sql
alter table demo.accounts
    rename constraint num_less_than_million_constraint_with_length_63_1234567890_1234 to balance_less_than_million_chk;

alter materialized view demo."accounts-materialized-view-with-length-63-1234567890-1234567890"
    rename to accounts_mv;
```

Помните, что для автоматически генерируемых имён (первичные ключи, последовательности `serial`-столбцов, индексы)
PostgreSQL может выйти за пределы 63 байт и обрезать их — закладывайте это при выборе имени самой таблицы и её столбцов.
