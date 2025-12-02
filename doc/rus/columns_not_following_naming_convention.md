# Проверка наличия столбцов, имена которых не соответствуют соглашению об именовании

Проверка находит имена столбцов в таблицах БД, которые необходимо экранировать двойными кавычками в SQL запросах.

## Почему нужно уделять этому внимание

- [Соглашение об именовании](https://postgrespro.ru/docs/postgresql/17/sql-syntax-lexical#SQL-SYNTAX-IDENTIFIERS)

Следует избегать использования имён столбцов, требующих обрамления двойными кавычками.
Это неудобно и может приводить [к неочевидным ошибкам](https://lerner.co.il/2013/11/30/quoting-postgresql/).  
Смотри также [wiki.postgresql.org](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_upper_case_table_or_column_names).

## SQL запрос

- [columns_not_following_naming_convention.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_not_following_naming_convention.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists "bad-demo";

create table if not exists "bad-demo"."bad-table"(
    "bad-id" serial not null primary key
);

create table if not exists "bad-demo"."bad-table-two"(
    "bad-ref-id" int not null primary key,
    description  text
);

create table if not exists "bad-demo"."one-partitioned"(
    "bad-id" bigserial not null primary key
) partition by range ("bad-id");

create table if not exists "bad-demo"."one-default"
    partition of "bad-demo"."one-partitioned" default;
```

## Как исправить

Аккуратно переименуйте столбцы и приведите их имена в соответствие с соглашением об именовании.  
Если ваша БД работает online и простой недопустим,
то вместо переименования столбца используйте [подход с созданием нового столбца](https://habr.com/ru/companies/karuna/articles/568240/)
и постепенным переходом на его использование.
