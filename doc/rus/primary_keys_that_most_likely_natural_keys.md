# Проверка наличия первичных ключей, которые скорее всего являются естественными ключами

## Почему следует обращать внимание на такие первичные ключи?

Естественный ключ - это ключ, который формируется из атрибутов, уже существующих в реальном мире.
Может создаваться ложное впечатление, что естественный ключ является уникальным и подходит для использования в качестве первичного ключа таблицы.
К сожалению, очень часто внешние ключи могут терять свойство уникальности со временем в силу политических, законодательных или иных изменений.
Предпочтительнее использовать суррогатные (искусственные, синтетические) ключи.

- [Вы пожалеете об использовании естественных ключей](https://habr.com/ru/articles/819619/)
- [7 Database Design Mistakes to Avoid (With Solutions)](https://www.youtube.com/watch?v=s6m8Aby2at8)

## SQL запрос

- [primary_keys_that_most_likely_natural_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/primary_keys_that_most_likely_natural_keys.sql)

Суррогатные ключи должны иметь тип `smallint`, `int`, `bigint` или `uuid`.
Если первичный ключ содержит столбцы других типов, то это подозрительно и требует пристального внимания со стороны разработчика.

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.
Для секционированных таблиц список допустимых типов столбцов шире и включает в себя `date`, `time` и `timestamp`, которые часто используются для секционирования по диапазонам.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.good (
    id int not null primary key
);

create table if not exists demo."times-of-creation" (
    "time-of-creation" timestamptz not null primary key
);

create table if not exists demo.t2_composite (
    passport_series text not null,
    passport_number text not null,
    primary key (passport_series, passport_number)
);

create table if not exists demo.t3_composite (
    app_id uuid not null,
    app_number text not null,
    primary key (app_id, app_number)
);

create table if not exists demo.tp_good (
    creation_date timestamp not null,
    entity_id uuid not null,
    primary key (creation_date, entity_id)
) partition by range (creation_date);

create table if not exists demo."tp_good-default" partition of demo.tp_good default;

create table if not exists demo.tp(
    creation_date timestamp not null,
    ref_type varchar(36) not null,
    entity_id varchar(36) not null,
    primary key (creation_date, ref_type, entity_id)
) partition by range (creation_date);

create table if not exists demo.tp_default partition of demo.tp default;
```
