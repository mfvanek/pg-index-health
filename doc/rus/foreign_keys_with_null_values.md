# Проверка наличия составных внешних ключей с nullable колонками без опции `MATCH FULL`

Если внешний ключ состоит из нескольких колонок, и часть из них может принимать значение `NULL`,
то в эту таблицу могут добавляться данные, которые отсутсвуют в целевой (referenced) таблице.

Дело в том, что PostgreSQL по умолчанию создаёт внешние ключи с опцией `MATCH SIMPLE`,
которая допускает, чтобы любой из столбцов внешнего ключа был `NULL`.
И если хотя бы один из них `NULL`, строка не обязана иметь соответствие в целевой таблице.

Исправить такое поведение позволяет опция `MATCH FULL`.
Она не допускает, чтобы один столбец составного внешнего ключа был `NULL`,
если только все столбцы внешнего ключа не `NULL`.

Подробности в [официальной документации](https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK)
и [статье на Хабре](https://habr.com/ru/articles/803841/).

## SQL запрос

- [foreign_keys_with_null_values.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_with_null_values.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table demo.referenced_table
(
    id    int not null,
    value text not null,
    primary key (id, value)
);

-- Заполнение справочника
insert into demo.referenced_table (id, value) values (10, '10');
insert into demo.referenced_table (id, value) values (20, '20');

create table demo.referencing_bad_table
(
    rbt_id integer not null,
    rbt_value text, -- nullable
    constraint "referencing-bad-table-fk" foreign key (rbt_id, rbt_value)
        references demo.referenced_table (id, value)
);

-- Добавление данных
-- Из-за того, что поле rbt_value может содержать null, обе записи будут добавлены в таблицу.
-- Если бы для constraint referencing_bad_table_fk установили MATCH FULL, то вторая запись не была бы добавлена.
-- https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK
insert into demo.referencing_bad_table (rbt_id, rbt_value) values (20, '20');
insert into demo.referencing_bad_table (rbt_id, rbt_value) values (30, null);

table demo.referencing_bad_table;

create table demo.referencing_good_table
(
    rgt_id integer not null,
    rgt_value text, -- nullable
    constraint referencing_good_table_fk foreign key (rgt_id, rgt_value)
        references demo.referenced_table (id, value) match full
);

insert into demo.referencing_good_table (rgt_id, rgt_value) values (20, '20');
-- insert into demo.referencing_good_table (rgt_id, rgt_value) values (30, null); -- error!

table demo.referencing_good_table;

-- Для секционированных таблиц

create table if not exists demo.referencing_bad_table_partitioned(
    rbt_id integer not null,
    rbt_value text, -- nullable
    created_at timestamptz not null default current_timestamp,
    constraint "referencing_bad_table_partitioned-fk" foreign key (rbt_id, rbt_value)
        references demo.referenced_table (id, value)
) partition by range (created_at);

create table if not exists demo.referencing_bad_table_default
    partition of demo.referencing_bad_table_partitioned default;

insert into demo.referencing_bad_table_partitioned (rbt_id, rbt_value) values (20, '20');
insert into demo.referencing_bad_table_partitioned (rbt_id, rbt_value) values (30, null);

table demo.referencing_bad_table_partitioned;
```

## Как исправить

Для составных внешних ключей, содержащих nullable колонки, используйте опцию `MATCH FULL`.
