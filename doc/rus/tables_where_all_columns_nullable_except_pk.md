# Проверка наличия таблиц, в который все столбцы за исключением первичного ключа являются nullable столбцами

## Зачем искать таблицы, где все колонки (кроме PK) допускают `NULL`?

- Качество данных
  * Указывает на слабое моделирование и отсутствие ограничений
  * Повышает риск неконсистентных и бесполезных записей
- Производительность
  * Дополнительные накладные расходы на хранение (NULL-битовые карты, пустые строки)
  * Индексы и запросы по таким колонкам работают неэффективно
- Сопровождение
  * Код может быть перегружен проверками на NULL
  * Растёт сложность тестирования и риск ошибок
  * Трудно накладывать новые ограничения и мигрировать данные

## SQL запрос

- [tables_where_all_columns_nullable_except_pk.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_where_all_columns_nullable_except_pk.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.bad_design (
    product_type text not null,
    description text,
    product_subtype text not null,
    primary key (product_type, product_subtype)
);

create table if not exists demo.no_pk (
    description text,
    product_type text,
    product_subtype text
);

create table if not exists demo.only_pk (
    id bigint generated always as identity primary key
);

create table if not exists demo."good-design"
(
    description text not null,
    id bigint generated always as identity primary key
);

create table if not exists demo."bad-design_partitioned"
(
    description text,
    id uuid not null primary key,
    created_at timestamptz
) partition by hash (id);

create table if not exists demo."bad-design_partitioned_hash_p0"
    partition of demo."bad-design_partitioned" for values with (modulus 4, remainder 0);
```
