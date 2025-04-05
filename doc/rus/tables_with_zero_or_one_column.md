# Проверка наличия таблиц без столбцов или с одним столбцом

## Почему стоит отслеживать такие таблицы

Обычно это указывает на плохой дизайн таблиц в БД или наличие мусора.  
Таблицы без столбцов следует удалить навсегда.  
Таблицы с одним столбцом имеет смысл перепроектировать и расширить или объединить с другой таблицей.

Если вам действительно нужна таблица с одним столбцом, например,
в качестве глобального индекса для секционированных таблиц, просто игнорируйте результаты этой проверки.

## SQL запрос

- [tables_with_zero_or_one_column.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_zero_or_one_column.sql)

## Тип проверки

- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)

## Поддержка секционированных таблиц

Поддерживает секционированные таблицы.
Проверка выполняется на самой секционированной таблице (родительской). Отдельные секции (потомки) игнорируются.

## Скрипт для воспроизведения

```sql
create schema if not exists demo;

create table if not exists demo.empty
(
);

create table if not exists demo.one
(
    ref_type int not null primary key
);

create table if not exists demo.two
(
    ref_type    int not null primary key,
    description text
);

create table if not exists demo.one_partitioned
(
    ref_type bigserial not null primary key
) partition by range (ref_type);

create table if not exists demo.one_default partition of demo.one_partitioned default;
```
