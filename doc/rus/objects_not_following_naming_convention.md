# Проверка наличия объектов, имена которых не соответствуют соглашению об именовании

Проверка находит имена объектов БД, которые необходимо экранировать двойными кавычками в SQL запросах.

## Почему нужно уделять этому внимание

- [Соглашение об именовании](https://postgrespro.ru/docs/postgresql/17/sql-syntax-lexical#SQL-SYNTAX-IDENTIFIERS)

Следует избегать использования идентификаторов, требующих обрамления двойными кавычками.
Это неудобно и может приводить [к неочевидным ошибкам](https://lerner.co.il/2013/11/30/quoting-postgresql/).  
Смотри также [wiki.postgresql.org](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_upper_case_table_or_column_names).

## Как могут появиться такие идентификаторы?

Если вы используете Liquibase и XML синтаксис, то легко можете столкнуться с некорректными именами объектов БД:

```xml
<changeSet author="author" id="add-index-on-task-expiration-date-status-v2">
    <createIndex tableName="my_task" indexName="idx-my-task-expired-at-status">
        <column name="task_expired_at"/>
        <column name="task_status"/>
    </createIndex>
</changeSet>
```

Здесь проблема в названии индекса `idx-my-task-expired-at-status`.
Вместо нижнего подчеркивания `_` были использованы дефисы `-`.
Такая миграция применится без ошибок, но в дальнейшем любое обращение к этому индексу из SQL будет требовать двойных кавычек:

```sql
reindex index concurrently "idx-my-task-expired-at-status";
```

## SQL запрос

- [objects_not_following_naming_convention.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/objects_not_following_naming_convention.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется как на самой секционированной таблице (родительской), так и на отдельных секциях (потомках).

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

alter table if exists "bad-demo"."bad-table-two"
    add constraint "bad-table-two-fk-bad-ref-id" foreign key ("bad-ref-id") references "bad-demo"."bad-table" ("bad-id");

create table if not exists "bad-demo"."one-partitioned"(
    "bad-id" bigserial not null primary key
) partition by range ("bad-id");

create table if not exists "bad-demo"."one-default"
    partition of "bad-demo"."one-partitioned" default;

create or replace function "bad-demo"."bad-add"(a integer, b integer) returns integer
as
'select $1 + $2;'
    language sql
    immutable
    returns null on null input;
```
