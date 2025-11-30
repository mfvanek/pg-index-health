# Проверка наличия таблиц, использующих наследование

Наследование — это устаревший и неудачный механизм. Не следует использовать его при проектировании современных БД.  
Вместо наследования рассмотрите другие варианты организации таблиц, например, секционирование.

Смотри также https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_table_inheritance

## SQL запрос

- [tables_with_inheritance.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_inheritance.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Не применима для секционированных таблиц. Наследование и секционирование не могут быть использованы вместе.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table demo.parent_table(
    id   bigint generated always as identity primary key,
    info text
);

create table demo.child_table(
    extra_info text
) inherits (demo.parent_table);

create table demo."second-child_table"(
    extra_info2 text
) inherits (demo.child_table);

create table if not exists demo.one_partitioned(
    ref_type bigserial not null primary key
) partition by range (ref_type);

create table if not exists demo.one_default
    partition of demo.one_partitioned default;
```
