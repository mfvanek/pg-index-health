# Проверка наличия функций без описания

## Почему нужно добавлять описание функций

С описанием легче применить функцию правильно и найти возможные ошибки в ее определении.
Наличие описания у функций\процедур упрощает их поддержку и модификацию в будущем.

## SQL запрос

- [functions_without_description.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/functions_without_description.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Не применима для секционированных таблиц.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.stats
(
    id bigint not null primary key,
    weight numeric not null,
    age integer not null,
    inserted timestamp with time zone not null
);

create function abs_reference (chosen_age integer) returns numeric as $$
    select percentile_cont(0.5) within group (order by weight) filter (where age = chosen_age)
        from demo.stats;
$$ language sql;

create table if not exists demo."stats_partitioned"
(
    id bigint not null primary key,
    weight numeric not null,
    age integer not null,
    inserted timestamp with time zone not null
) partition by range (age);

create function abs_reference_part (chosen_age integer) returns numeric as $$
    select percentile_cont(0.5) within group (order by weight) filter (where age = chosen_age)
        from demo."stats_partitioned";
$$ language sql;

comment on function abs_reference_part (integer) is ' ';

create table if not exists demo."stats_partitioned_12"
    partition of demo."account_without_fk_partitioned"
    for values from (12) to (13);
```
