# Проверка внешних ключей, у которых есть расхождения в типах столбцов

## Почему типы столбцов у внешних ключей должны совпадать

Типы колонок в ссылающемся и целевом отношении должны совпадать.
Колонка с типом `integer` должна ссылаться на колонку с типом `integer`.
Это исключает лишние конвертации на уровне СУБД и в коде приложения, снижает количество ошибок,
которые могут появляться из-за несоответствия типов в будущем.

Можно возразить, достаточно чтобы в ссылающемся отношении типы колонок были совместимы и не меньше, чем в целевом отношении.
Например, колонка с типом `bigint` может ссылаться на колонку с типом `integer`.
**Так делать не следует**.
Необходимо учитывать, что разные типы будут попадать в код приложения, особенно при автогенерации моделей.

## SQL запрос

- [foreign_keys_with_unmatched_column_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_with_unmatched_column_type.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

# Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.client
(
    id integer not null primary key,
    first_name text,
    last_name text
);

create table if not exists demo."account_with_other_type_fk_partitioned"
(
    id bigint not null primary key,
    account_number varchar(50) not null unique,
    client_id bigint not null references demo.client (id),
    created timestamp with time zone not null
) partition by range (created);

create table if not exists demo."account_with_other_type_fk_partitioned_Q3"
    partition of demo."account_without_fk_partitioned"
    for values from ('2025-07-01') to ('2025-10-01');
```
