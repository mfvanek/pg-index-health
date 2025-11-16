# Проверка наличия таблиц без описания

## Почему нужно добавлять описание таблицам

Разработчикам легче ориентироваться, из каких таблиц брать нужные данные.
Наличие описания у таблиц упрощает их поддержку и модификацию в будущем.

## SQL запрос

- [tables_without_description.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_description.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."table_without_description"
(
    id integer not null primary key,
    first_name text,
    last_name text
);

comment on table demo."table_without_description" is '   ';
                    
create table if not exists demo."table_without_description_partitioned"
(
    id integer not null primary key,
    first_name text,
    last_name text
) partition by range (id);

comment on table demo."table_without_description_partitioned" is '';

create table if not exists demo."table_without_description_partitioned_1_10"
    partition of demo."table_without_description"
    for values from (1) to (10);
```
