# Проверка наличия индексов, которые имеют избыточный предикат с предложением where

Эта проверка тесно связана с проверкой [indexes_with_null_values](indexes_with_null_values.md).

Бывает, что разработчики начинают копировать и редактировать запросы на создание индексов.
Это приводит к появлению запросов с предикатами вида `where <column> is not null` даже на тех столбцах,
которые изначально имеют характеристику `not null`.

Такие индексы:
- содержат избыточный предикат, который вычисляется каждый раз;
- не могут быть использованы в качестве основы для внешнего ключа, если создаются с ключевым словом `unique`.

## SQL запрос

- [indexes_with_unnecessary_where_clause.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_unnecessary_where_clause.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.t1(
    id     bigint not null primary key,
    id_ref bigint not null);

create index if not exists idx_t1_id_ref on demo.t1 (id_ref) where id_ref is not null;

create table if not exists demo.t2(
    "first-ref" bigint not null,
    second_ref  bigint not null,
    t1_id       bigint references demo.t1 (id));

create index if not exists "idx_t2_first-ref_second_ref" on demo.t2 (second_ref, "first-ref") where "first-ref" is not null;

create index if not exists idx_t2_id_ref on demo.t2 (t1_id) where t1_id is not null;

create index if not exists idx_second_ref_t1_id on demo.t2 (t1_id, second_ref) where t1_id is not null;
```
