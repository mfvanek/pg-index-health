# Проверка наличия колонок с типом больших объектов (`oid` или `lo`)

Колонки с типом `oid` или `lo` хранят дескриптор (идентификатор объекта), указывающий на большой объект,
управляемый механизмом больших объектов PostgreSQL (`pg_largeobject`).
Каждое чтение или запись такой колонки требует дополнительного обращения к `pg_largeobject`,
что увеличивает нагрузку на I/O и усложняет работу с данными по сравнению со встроенным хранилищем.

Тип `lo` — это домен над `oid`, предоставляемый расширением `lo`.
Он был создан как обходное решение для ситуации, когда стандартный механизм внешних ключей PostgreSQL
не удаляет автоматически осиротевшие большие объекты при удалении строк-владельцев.
Даже при использовании триггерной очистки расширения `lo` косвенная модель хранения остаётся
менее удобной и сложнее поддаётся анализу.

Для большинства задач `bytea` (для бинарных данных) или `text` (для символьных данных) — лучшая альтернатива:
данные хранятся непосредственно в TOAST-хранилище таблицы, поддерживают стандартное сжатие PostgreSQL
и не требуют специальной обработки при удалении.

Подробности в [официальной документации по большим объектам](https://www.postgresql.org/docs/current/largeobjects.html)
и [документации по расширению lo](https://www.postgresql.org/docs/current/lo.html).

## SQL запрос

- [columns_with_blob_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_blob_type.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

-- lo domain type requires the extension (installs the 'lo' domain over oid)
create extension if not exists lo;

-- raw oid column (large object handle)
create table if not exists demo."document-bad" (
    id bigserial primary key,
    title text not null,
    "content-bad" oid not null
);

-- lo domain column; trigger is best practice but not required for detection
create table if not exists demo.image (
    id bigserial primary key,
    title text not null,
    raster lo
);

create or replace trigger t_raster
    before update or delete on demo.image
    for each row execute function lo_manage(raster);

-- two blob columns on the same table → two rows in the result set
create table if not exists demo.media_file (
    id bigserial primary key,
    name text not null,
    thumbnail oid,
    full_image lo not null
);

create or replace trigger t_full_image
    before update or delete on demo.media_file
    for each row execute function lo_manage(full_image);

-- partitioned
create table if not exists demo.attachment (
    id bigserial not null,
    created_at date not null,
    file_data oid
) partition by range (created_at);

create table if not exists demo.attachment_2024
    partition of demo.attachment for values from ('2024-01-01') to ('2025-01-01');

-- bytea is the recommended replacement — stored inline, no pg_largeobject reads
create table if not exists demo.binary_data (
    id bigserial primary key,
    title text not null,
    content bytea not null
);
```

## Как исправить

Замените колонки типа `oid` или `lo` на `bytea` для бинарных данных или `text`/`varchar` для символьных данных.

```sql
alter table your_schema.your_table
    alter column your_column type bytea using null; -- существующие большие объекты перенесите отдельно
```

Если большие объекты необходимо сохранить по устаревшим требованиям, задокументируйте причину и убедитесь,
что триггер очистки расширения `lo` настроен для предотвращения появления осиротевших записей в `pg_largeobject`.
