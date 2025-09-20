# Проверка наличия таблиц, где столбцы первичного ключа стоят не первыми в списке

## Почему следует обращать внимание на такие первичные ключи?

Размещение первичного ключа (**primary key**) первым столбцом в таблице не является технической необходимостью,
но важно как часть стиля и соглашений:

* Читаемость: сразу видно ключ при просмотре схемы; упрощает понимание структуры.
* Согласованность: единый шаблон (**primary key** идёт первым) снижает «сюрпризы» при работе с разными схемами.
* Запросы: в `select` запросах **primary key** всегда будет первой колонкой, что упрощает отладку и повышает наглядность.
* Исторические практики: это устоявшаяся традиция, которая помогает поддерживать единый стиль во всех таблицах.

## SQL запрос

- [tables_where_primary_key_columns_not_first.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_where_primary_key_columns_not_first.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.good_pk (
    product_type text not null,
    description text not null,
    product_subtype text not null,
    primary key (product_type, product_subtype)
);

create table if not exists demo.not_good_pk (
    description text not null,
    product_type text not null,
    product_subtype text not null,
    primary key (product_type, product_subtype)
);

create table if not exists demo."bad-pk" (
    description text not null,
    id bigint generated always as identity primary key
);

create table if not exists demo."bad-pk_partitioned" (
    description text not null,
    id uuid not null primary key,
    created_at timestamptz not null default now()
) partition by hash (id);

create table if not exists demo."bad_pk_partitioned_hash_p0"
    partition of demo."bad-pk_partitioned" for values with (modulus 4, remainder 0);
```
