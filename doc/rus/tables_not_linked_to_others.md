# Проверка наличия таблиц, не связанных с другими таблицами

## Почему таблицы без связей нужно отслеживать

Отсутствие связей в таблице может быть следствием ошибки: забыли добавить внешние ключи.
Все таблицы в базе данных отражают модель данных - связи нужны для консистентных изменений в таблицах.
Также внешние ключи дают возможность быстро и верно находить данные, относящиеся к одной сущности.

Если таблицы без связей созданы намеренно, то их нужно просто проигнорировать.

## SQL запрос

- [tables_not_linked_to_others.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_not_linked_to_others.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo."client_without_fk"
(
    id bigint not null primary key,
    first_name text,
    last_name text
);

create table if not exists demo."account_without_fk_partitioned"
(
    id bigint not null primary key,
    account_number varchar(50) not null unique,
    client_id bigint not null,
    created timestamp with time zone not null
) partition by range (created);

create table if not exists demo."account_without_fk_partitioned_Q3"
    partition of demo."account_without_fk_partitioned"
    for values from ('2025-07-01') to ('2025-10-01');
```

