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

create table if not exists demo."table_without_primary_key"
(
    id bigint not null,
    first_name text,
    last_name text
);

create table if not exists demo."table_without_primary_key_partitioned"
(
    id integer not null,
    first_name text,
    last_name text
) partition by hash (name);

create table if not exists demo."table_without_primary_key_partitioned_hash_p0"
    partition of demo."table_without_primary_key_partitioned"
    for values with (modulus 4, remainder 0);
```
