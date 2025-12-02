# Проверка наличия столбцов типа serial, которые не являются первичным ключом

## Особенности типа serial в PosgreSQL

Типы данных smallserial, serial и bigserial - это синтаксический сахар.
Они реализованы через [последовательности целых чисел](https://postgrespro.ru/docs/postgresql/17/datatype-numeric).

## Почему serial не стоит использовать

На столбце создается последовательность со значением по умолчанию, из которого будут вычисляться последующие значения.
В БД создаются лишние объекты, которые на самом деле не нужны.
В современных версиях PostgreSQL [даже для первичных ключей лучше не использовать serial-типы](primary_keys_with_serial_types.md).

## SQL запрос

- [columns_with_serial_types.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_serial_types.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.table_with_serial_column(
    ref_type smallserial,
    ref_value serial,
    real_client_id bigserial    
);

create table if not exists demo.table_with_serial_column_partitioned(
    ref_type smallserial,
    ref_value serial,
    creation_date timestamp with time zone not null,
    real_client_id bigserial    
) partition by range (creation_date);

create table if not exists demo.table_with_serial_column_partitioned_q3
    partition of demo.table_with_serial_column_partitioned
        for values from ('2025-07-01') to ('2025-10-01');
```
