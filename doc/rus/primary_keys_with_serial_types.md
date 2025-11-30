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
create table if not exists demo."table_with_serial_pk"
(
    id bigserial not null primary key,
    first_name text,
    last_name text
);

create table if not exists demo."table_with_serial_pk_partitioned"
(
    id serial not null primary key,
    first_name text,
    last_name text
) partition by hash (name);

create table if not exists demo."table_with_serial_pk_partitioned_hash_p0"
    partition of demo."table_without_primary_key_partitioned"
    for values with (modulus 4, remainder 0);
```
