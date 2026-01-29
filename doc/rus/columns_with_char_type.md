# Проверка наличия столбцов с типом char(n) в таблицах

Использование типа `char(n)` или `character(n)`, скорее всего, является ошибкой и почти всегда должно быть заменено на тип `text`.

## Почему не стоит использовать

Любая строка, которую вы вставляете в поле типа `char(n)`, будет дополнена пробелами до заявленной ширины.
Заполнение значения в столбце пробелами приводит к потере места, но не ускоряет операции с этим значением.

Дополнительные источники:
- [Character Types ](https://www.postgresql.org/docs/current/datatype-character.html)
- [Don't use char(n)](https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_char(n))
- [Squawk ban-char-field](https://squawkhq.com/docs/ban-char-field)

## SQL запрос

- [columns_with_char_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_char_type.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create table if not exists demo.orders
(
    id bigint primary key generated always as identity,
    user_id bigint not null,
    shop_id bigint not null,
    status int not null,
    created_at timestamptz not null default current_timestamp,
    user_sex char not null,
    user_name char(100) not null,
    user_surname character(200) not null ,
    user_patronymic bpchar(100),
    user_second_name bpchar
);

create table if not exists demo.orders_partitioned
(
    id         bigint not null generated always as identity,
    user_id    bigint      not null,
    shop_id    bigint      not null,
    status     int         not null,
    created_at timestamptz not null default current_timestamp,
    user_sex char not null,
    user_name char(100) not null,
    user_surname character(200) not null ,
    user_patronymic bpchar(100),
    user_second_name bpchar,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.orders_default
    partition of demo.orders_partitioned default;
```

## Как исправить

Измените тип столбца на `text`. Не забудьте внести необходимые правки в приложение, работающее с БД.
