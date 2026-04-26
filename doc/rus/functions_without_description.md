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

create or replace function demo.add(a integer, b integer) returns integer
as 'select $1 + $2;'
    language sql
    immutable
    returns null on null input;

create or replace function demo.add(a int, b int, c int) returns int
as 'select $1 + $2 + $3;'
    language sql
    immutable
    returns null on null input;
```

## Как исправить

Добавьте человекочитаемые описания ко всем функциям.
