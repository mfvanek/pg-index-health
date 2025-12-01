# Проверка наличия столбцов без описания

## Почему нужно добавлять описание столбцам

Описание столбцов делает понятным бизнес-смысл хранящихся там значений.
Разработчик\аналитик\тестировщик сделает меньше ошибок и быстрее выполнит свою работу,
если будет понимать, какие есть ограничения на данные в столбцах со стороны бизнеса и откуда они берутся (какое у них назначение).
Наличие описания у столбцов упрощает их поддержку и модификацию в будущем.

## SQL запрос

- [columns_without_description.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_without_description.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.


## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.column_without_description(
    id integer not null primary key,
    ref_type integer,
    ref_value varchar(64)
);

comment on column demo.column_without_description.ref_value is '   ';

create table if not exists demo.column_without_description_partitioned(
    id integer not null primary key,
    ref_type integer,
    ref_value varchar(64)
) partition by range (id);

comment on column demo.column_without_description_partitioned.ref_type is '';

create table if not exists demo.column_without_description_partitioned_1_10
    partition of demo.column_without_description_partitioned
        for values from (1) to (10);

comment on column demo."column_without_description_partitioned_1_10".ref_value is '';
```
