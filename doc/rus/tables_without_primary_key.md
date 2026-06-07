# Проверка наличия таблиц без первичных ключей

## Особенности создания таблиц

PostgreSQL позволяет создавать таблицы без первичного ключа, но такой дизайн может приводить к проблемам в будущем,
связанным с обслуживанием таблиц.
Например, [pg_repack](https://github.com/reorg/pg_repack) не может обрабатывать таблицы без первичного ключа или иного ограничения уникальности.
Аналогичная ситуация с [логической репликацией](https://postgrespro.ru/docs/postgresql/17/logical-replication-publication) -
для эффективной работы в реплицируемых таблицах требуется наличие первичного ключа или иного ограничения уникальности.

## SQL запрос

- [tables_without_primary_key.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_primary_key.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется как на самой секционированной таблице (родительской), так и на каждой секции.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- Для обычных (не секционированных) таблиц

-- Таблица без первичного ключа
create table if not exists demo.bad_clients (
    id bigint not null,
    name varchar(255) not null,
    real_client_id integer,
    email varchar(200),
    phone varchar(51)
);

-- Для секционированных таблиц

-- Секционированная таблица без первичного ключа.
-- Проверка найдёт как саму родительскую таблицу, так и каждую её секцию.
create table if not exists demo.entity_reference(
    ref_type varchar(32) not null,
    ref_value varchar(64) not null,
    creation_date timestamptz not null,
    entity_id varchar(64) not null
) partition by range (creation_date);

create table if not exists demo.entity_reference_default
    partition of demo.entity_reference default;
```

## Как исправить

Добавьте первичный ключ в таблицу.

Если в таблице уже есть колонка (или набор колонок), однозначно идентифицирующая строку, используйте её:

```sql
alter table demo.bad_clients
    add primary key (id);
```

Если естественного ключа нет, добавьте суррогатный ключ на основе колонки с автоинкрементом:

```sql
alter table demo.bad_clients
    add column new_id bigint generated always as identity primary key;
```

Для секционированных таблиц первичный ключ должен включать все колонки секционирования.
Создавайте его на родительской таблице — он автоматически распространится на все секции:

```sql
alter table demo.entity_reference
    add primary key (ref_type, ref_value, creation_date, entity_id);
```
