# Проверка дублирующихся индексов в таблицах

## Почему индекс может задублироваться

Индекс на первичный ключ и индексы на ограничения уникальности создаются [автоматически](https://postgrespro.ru/docs/postgresql/17/indexes-unique).
Если подобного рода индексы были созданы еще и вручную, то [получаются дубликаты](https://postgrespro.ru/docs/postgresql/17/sql-createindex).
PostgreSQL не умеет отслеживать такие ситуации и предотвращать создание дубликатов.

## Почему нужно избавляться от дублирующихся индексов

При изменении данных обновляется [каждый индекс](https://postgrespro.ru/docs/postgresql/17/btree).
Таким образом дубликаты снижают производительность, занимают место на диске и
требуют регулярного обслуживания (вакуумирование, реиндексация при распухании).

## SQL запрос

- [duplicated_indexes.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_indexes.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."table_with_duplicated_indexes"
(
    id bigint not null primary key,
    first_name text,
    last_name text,
    passport text unique not null
);

create index if not exists i_duplicated on demo."table_with_duplicated_indexes" (id);

create index if not exists i_passport_duplicated on demo."table_with_duplicated_indexes" (passport);

create table if not exists demo."table_with_duplicated_indexes_partitioned"
(
    id bigint not null,
    first_name text,
    last_name text,
    passport text unique not null
) partition by hash (name);

create index if not exists i_duplicated_p on demo."table_with_duplicated_indexes_partitioned" (id);

create index if not exists i_passport_duplicated_p on demo."table_with_duplicated_indexes_partitioned" (passport);

create table if not exists demo."table_with_duplicated_indexes_partitioned_hash_p0"
    partition of demo."table_with_duplicated_indexes_partitioned"
    for values with (modulus 4, remainder 0);
```
